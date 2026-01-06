package com.clarice.burrow.data.repository

import com.clarice.burrow.data.local.TokenManager
import com.clarice.burrow.data.remote.*
import com.clarice.burrow.ui.model.auth.*
import com.clarice.burrow.ui.model.common.ApiResponse
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    // Register new user
    suspend fun register(
        username: String,
        password: String,
        name: String,
        birthdate: String? = null,
        defaultSoundDuration: Int? = null,
        reminderTime: String? = null,
        gender: String? = null
    ): NetworkResult<ApiResponse<User>> {
        val request = RegisterRequest(
            username = username,
            password = password,
            name = name,
            birthdate = birthdate,
            defaultSoundDuration = defaultSoundDuration,
            reminder_time = reminderTime,
            gender = gender
        )

        return safeApiCall { apiService.register(request) }
    }

    // Login user
    suspend fun login(
        username: String,
        password: String
    ): NetworkResult<AuthResponse> {
        val request = LoginRequest(username, password)
        val result = safeApiCall { apiService.login(request) }

        // Save token if successful
        if (result is NetworkResult.Success) {
            result.data?.let { authResponse ->
                tokenManager.saveToken(authResponse.token)
                tokenManager.saveUserInfo(
                    userId = authResponse.user.userId,
                    username = authResponse.user.username
                )
                // Reset the API client so a new one is created with the fresh token
                RetrofitClient.resetClient()
            }
        }

        return result
    }

    // Logout user
    suspend fun logout() {
        tokenManager.clearAll()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return tokenManager.isLoggedIn()
    }

    /**
     * Get current user ID
     */
    fun getCurrentUserId(): Flow<Int?> {
        return tokenManager.getUserId()
    }

    /**
     * Get current username
     */
    fun getCurrentUsername(): Flow<String?> {
        return tokenManager.getUsername()
    }
}