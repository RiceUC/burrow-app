// File: app/src/main/java/com/clarice/burrow/ui/viewmodel/ProfileViewModel.kt
package com.clarice.burrow.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.data.remote.NetworkResult
import com.clarice.burrow.data.remote.RetrofitClient
import com.clarice.burrow.data.repository.UserRepository
import com.clarice.burrow.ui.model.auth.User
import kotlinx.coroutines.launch

/**
 * ProfileViewModel - Handles user profile operations
 */
class ProfileViewModel(context: Context) : ViewModel() {

    private val apiService = RetrofitClient.getApiService(context)
    private val userRepository = UserRepository(apiService)

    var profileState by mutableStateOf(ProfileState())
        private set

    init {
        // Remove automatic loading - let the screen trigger it
        // This prevents unnecessary API calls
    }

    // ==================== LOAD PROFILE ====================

    /**
     * Load user profile from backend
     * Call this explicitly from the screen
     */
    fun loadProfile() {
        // Prevent multiple simultaneous loads
        if (profileState.isLoading) {
            android.util.Log.w("ProfileViewModel", "Load already in progress, skipping")
            return
        }

        profileState = profileState.copy(isLoading = true, error = null)
        android.util.Log.d("ProfileViewModel", "Starting profile load...")

        viewModelScope.launch {
            try {
                val result = userRepository.getProfile()

                when (result) {
                    is NetworkResult.Success -> {
                        android.util.Log.d("ProfileViewModel", "Profile loaded successfully: ${result.data?.data}")
                        profileState = profileState.copy(
                            isLoading = false,
                            user = result.data?.data,
                            error = null
                        )
                    }
                    is NetworkResult.Error -> {
                        android.util.Log.e("ProfileViewModel", "Failed to load profile: ${result.message}")
                        profileState = profileState.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load profile"
                        )
                    }
                    is NetworkResult.Loading -> {
                        profileState = profileState.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Exception loading profile", e)
                profileState = profileState.copy(
                    isLoading = false,
                    error = "Failed to load profile: ${e.message}"
                )
            }
        }
    }

    // ==================== UPDATE PROFILE ====================

    /**
     * Update user profile
     */
    fun updateProfile(
        name: String? = null,
        birthdate: String? = null,
        defaultSoundDuration: Int? = null,
        reminderTime: String? = null,
        gender: String? = null,
        onSuccess: () -> Unit
    ) {
        profileState = profileState.copy(isUpdating = true, error = null)
        android.util.Log.d("ProfileViewModel", "Updating profile...")

        viewModelScope.launch {
            try {
                val result = userRepository.updateProfile(
                    name = name,
                    birthdate = birthdate,
                    defaultSoundDuration = defaultSoundDuration,
                    reminderTime = reminderTime,
                    gender = gender
                )

                when (result) {
                    is NetworkResult.Success -> {
                        android.util.Log.d("ProfileViewModel", "Profile updated successfully")
                        profileState = profileState.copy(
                            isUpdating = false,
                            user = result.data?.data,
                            error = null
                        )
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        android.util.Log.e("ProfileViewModel", "Failed to update profile: ${result.message}")
                        profileState = profileState.copy(
                            isUpdating = false,
                            error = result.message ?: "Failed to update profile"
                        )
                    }
                    is NetworkResult.Loading -> {
                        profileState = profileState.copy(isUpdating = true)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Exception updating profile", e)
                profileState = profileState.copy(
                    isUpdating = false,
                    error = "Failed to update profile: ${e.message}"
                )
            }
        }
    }

    // ==================== DELETE ACCOUNT ====================

    /**
     * Delete user account
     */
    fun deleteAccount(onSuccess: () -> Unit) {
        profileState = profileState.copy(isDeleting = true, error = null)
        android.util.Log.d("ProfileViewModel", "Deleting account...")

        viewModelScope.launch {
            try {
                val result = userRepository.deleteAccount()

                when (result) {
                    is NetworkResult.Success -> {
                        android.util.Log.d("ProfileViewModel", "Account deleted successfully")
                        profileState = profileState.copy(isDeleting = false)
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        android.util.Log.e("ProfileViewModel", "Failed to delete account: ${result.message}")
                        profileState = profileState.copy(
                            isDeleting = false,
                            error = result.message ?: "Failed to delete account"
                        )
                    }
                    is NetworkResult.Loading -> {
                        profileState = profileState.copy(isDeleting = true)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Exception deleting account", e)
                profileState = profileState.copy(
                    isDeleting = false,
                    error = "Failed to delete account: ${e.message}"
                )
            }
        }
    }

    // ==================== HELPERS ====================

    /**
     * Clear error message
     */
    fun clearError() {
        profileState = profileState.copy(error = null)
    }
}

/**
 * Profile UI State
 */
data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null
)