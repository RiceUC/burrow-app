package com.clarice.burrow.data.remote

import com.clarice.burrow.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor - Automatically adds Bearer token to requests
 * @param tokenManager TokenManager instance for retrieving auth tokens
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip token injection for public endpoints (login/register only)
        val url = originalRequest.url.encodedPath
        if (url.contains("/login") || url.contains("/register")) {
            return chain.proceed(originalRequest)
        }

        // Get token and add to header for all other endpoints
        val token = runBlocking {
            tokenManager.getToken().first()
        }

        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            // If no token, still proceed with request (server will return 401 if needed)
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}