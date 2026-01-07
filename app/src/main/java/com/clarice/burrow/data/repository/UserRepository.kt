package com.clarice.burrow.data.repository

import com.clarice.burrow.data.remote.ApiService
import com.clarice.burrow.data.remote.NetworkResult
import com.clarice.burrow.data.remote.safeApiCall
import com.clarice.burrow.ui.model.auth.UpdateProfileRequest
import com.clarice.burrow.ui.model.auth.User
import com.clarice.burrow.ui.model.common.ApiResponse

class UserRepository(
    private val apiService: ApiService
) {

    // Get user profile
    suspend fun getProfile(): NetworkResult<ApiResponse<User>> {
        return safeApiCall { apiService.getProfile() }
    }

    // Update user profile
    suspend fun updateProfile(
        name: String? = null,
        birthdate: String? = null,
        defaultSoundDuration: Int? = null,
        reminderTime: String? = null,
        gender: String? = null
    ): NetworkResult<ApiResponse<User>> {
        val request = UpdateProfileRequest(
            name = name,
            birthdate = birthdate,
            defaultSoundDuration = defaultSoundDuration,
            reminderTime = reminderTime,
            gender = gender
        )

        return safeApiCall { apiService.updateProfile(request) }
    }

    // Delete user account
    suspend fun deleteAccount(): NetworkResult<ApiResponse<String>> {
        return safeApiCall { apiService.deleteAccount() }
    }
}