package com.clarice.burrow.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response

// NetworkResult (Sealed class for handling API responses)
// Provides a clean way to handle Success, Error, and Loading states
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()
}

// Generic error response from backend to encapsulate different error formats.
data class ErrorResponse(val errors: Any?, val message: String?)


// Extension function to safely handle API calls
// Wraps API responses in NetworkResult for consistent error handling

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

// Parse error message from backend response.
// This function is designed to handle multiple common error response formats.
private fun parseErrorMessage(errorBody: String?): String? {
    if (errorBody == null) return null
    val gson = Gson()

    return try {
        // Attempt to parse into a structured error object
        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)

        // 1. Check for a map of validation errors in "errors"
        if (errorResponse.errors is Map<*, *>) {
            return (errorResponse.errors as Map<*, *>).map { (key, value) ->
                val messages = (value as? List<*>)?.joinToString(", ") ?: value.toString()
                "$key: $messages"
            }.joinToString("\n")
        }

        // 2. Check for a list of errors in "errors"
        if (errorResponse.errors is List<*>) {
            return (errorResponse.errors as List<*>).joinToString(", ")
        }
        
        // 3. Check for a single error string in "errors"
        if (errorResponse.errors is String) {
            return errorResponse.errors
        }

        // 4. Check for a top-level "message" field
        if (errorResponse.message != null) {
            return errorResponse.message
        }
        
        // 5. Fallback for other JSON structures like {"error": "message"}
        val errorMap: Map<String, Any> = gson.fromJson(errorBody, object : TypeToken<Map<String, Any>>() {}.type)
        errorMap["error"]?.toString() ?: errorBody

    } catch (e: Exception) {
        // If JSON parsing fails, the body might be a plain string
        errorBody
    }
}