package com.clarice.burrow.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.data.local.TokenManager
import com.clarice.burrow.data.remote.NetworkResult
import com.clarice.burrow.data.remote.RetrofitClient
import com.clarice.burrow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AuthViewModel - Handles authentication logic for Login and Register
 */
class AuthViewModel(context: Context) : ViewModel() {

    private val apiService = RetrofitClient.getApiService(context)
    private val tokenManager = TokenManager(context)
    private val authRepository = AuthRepository(apiService, tokenManager)

    // UI States
    var loginState by mutableStateOf(LoginState())
        private set

    var registerState by mutableStateOf(RegisterState())
        private set

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        checkLoginStatus()
    }

    // ==================== LOGIN ====================

    /**
     * Update login username
     */
    fun updateLoginUsername(username: String) {
        loginState = loginState.copy(username = username, error = null)
    }

    /**
     * Update login password
     */
    fun updateLoginPassword(password: String) {
        loginState = loginState.copy(password = password, error = null)
    }

    /**
     * Perform login
     */
    fun login(onSuccess: () -> Unit) {
        // Validation
        if (loginState.username.isBlank()) {
            loginState = loginState.copy(error = "Username cannot be empty")
            return
        }

        if (loginState.password.isBlank()) {
            loginState = loginState.copy(error = "Password cannot be empty")
            return
        }

        loginState = loginState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = authRepository.login(
                username = loginState.username,
                password = loginState.password
            )

            when (result) {
                is NetworkResult.Success -> {
                    loginState = loginState.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                    _isLoggedIn.value = true
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    loginState = loginState.copy(
                        isLoading = false,
                        error = result.message ?: "Login failed"
                    )
                }
                is NetworkResult.Loading -> {
                    loginState = loginState.copy(isLoading = true)
                }
            }
        }
    }

    // ==================== REGISTER ====================

    /**
     * Update register fields
     */
    fun updateRegisterUsername(username: String) {
        registerState = registerState.copy(username = username, error = null)
    }

    fun updateRegisterPassword(password: String) {
        registerState = registerState.copy(password = password, error = null)
    }

    fun updateRegisterName(name: String) {
        registerState = registerState.copy(name = name, error = null)
    }

    fun updateRegisterBirthdate(birthdate: String) {
        registerState = registerState.copy(birthdate = birthdate, error = null)
    }

    fun updateRegisterGender(gender: String) {
        registerState = registerState.copy(gender = gender, error = null)
    }

    /**
     * Perform registration
     */
    fun register(onSuccess: () -> Unit) {
        // Validation
        if (registerState.username.isBlank()) {
            registerState = registerState.copy(error = "Username cannot be empty")
            return
        }

        if (registerState.username.length < 3) {
            registerState = registerState.copy(error = "Username must be at least 3 characters")
            return
        }

        if (registerState.password.isBlank()) {
            registerState = registerState.copy(error = "Password cannot be empty")
            return
        }

        if (registerState.password.length < 8) {
            registerState = registerState.copy(error = "Password must be at least 8 characters")
            return
        }

        if (registerState.name.isBlank()) {
            registerState = registerState.copy(error = "Name cannot be empty")
            return
        }

        registerState = registerState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = authRepository.register(
                username = registerState.username,
                password = registerState.password,
                name = registerState.name,
                birthdate = registerState.birthdate.ifBlank { null },
                gender = registerState.gender.ifBlank { null },
                defaultSoundDuration = 30 // Default value for sound duration
            )

            when (result) {
                is NetworkResult.Success -> {
                    registerState = registerState.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    registerState = registerState.copy(
                        isLoading = false,
                        error = result.message ?: "Registration failed"
                    )
                }
                is NetworkResult.Loading -> {
                    registerState = registerState.copy(isLoading = true)
                }
            }
        }
    }

    // ==================== LOGOUT ====================

    /**
     * Logout user
     */
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
            resetStates()
            onSuccess()
        }
    }

    // ==================== HELPERS ====================

    /**
     * Check if user is already logged in
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            authRepository.isLoggedIn().collect { isLoggedIn ->
                _isLoggedIn.value = isLoggedIn
            }
        }
    }

    /**
     * Reset all states
     */
    private fun resetStates() {
        loginState = LoginState()
        registerState = RegisterState()
    }

    /**
     * Clear login error
     */
    fun clearLoginError() {
        loginState = loginState.copy(error = null)
    }

    /**
     * Clear register error
     */
    fun clearRegisterError() {
        registerState = registerState.copy(error = null)
    }
}

/**
 * Login UI State
 */
data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

/**
 * Register UI State
 */
data class RegisterState(
    val username: String = "",
    val password: String = "",
    val name: String = "",
    val birthdate: String = "",
    val gender: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
