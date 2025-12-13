package com.clarice.burrow.data.remote

import retrofit2.Response

/**
 * NetworkResult - Sealed class for handling API responses
 * Provides a clean way to handle Success, Error, and Loading states
 */
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()
}

/**
 * Extension function to safely handle API calls
 * Wraps API responses in NetworkResult for consistent error handling
 *
 * Usage:
 * val result = safeApiCall { apiService.getSomething() }
 * when (result) {
 *     is NetworkResult.Success -> // handle success
 *     is NetworkResult.Error -> // handle error
 *     is NetworkResult.Loading -> // handle loading
 * }
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): NetworkResult<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            response.body()?.let {
                NetworkResult.Success(it)
            } ?: NetworkResult.Error("Empty response body")
        } else {
            // Parse error response from backend
            val errorBody = response.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Error: ${response.code()}"
            NetworkResult.Error(errorMessage)
        }
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "An unexpected error occurred")
    }
}

/**
 * Parse error message from backend response
 * Your backend returns errors in this format: { "errors": "error message" }
 */
private fun parseErrorMessage(errorBody: String?): String? {
    return try {
        errorBody?.let {
            val regex = """"errors"\s*:\s*"([^"]+)"""".toRegex()
            regex.find(it)?.groupValues?.get(1)
        }
    } catch (e: Exception) {
        errorBody
    }
}