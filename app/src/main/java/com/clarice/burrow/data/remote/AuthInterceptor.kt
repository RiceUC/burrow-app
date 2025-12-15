package com.clarice.burrow.data.remote

import com.clarice.burrow.data.local.TokenManager
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

        // Skip token injection for public endpoints
        val url = originalRequest.url.encodedPath
        if (url.contains("/login") || url.contains("/register") || url.contains("/refresh-token")) {
            return chain.proceed(originalRequest)
        }

        // Get access token and add to header
        val accessToken = runBlocking {
            tokenManager.getAccessTokenDirect()
        }

        val newRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}