package com.clarice.burrow.data.remote

import android.content.Context
import android.util.Log
import com.clarice.burrow.data.local.TokenManager
import com.clarice.burrow.ui.model.auth.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TokenRefreshInterceptor(
    private val context: Context,
    private val tokenManager: TokenManager,
    private val apiService: ApiService
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get current access token
        val accessToken = runBlocking {
            tokenManager.getAccessTokenDirect()
        }

        // Add token to request
        val authorizedRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        var response = chain.proceed(authorizedRequest)

        // If token expired (401), try to refresh
        if (response.code == 401 && accessToken != null) {
            Log.d("TokenRefreshInterceptor", "Token expired, attempting refresh...")
            
            synchronized(this) {
                // Get fresh token again in case another thread refreshed it
                val newAccessToken = runBlocking {
                    tokenManager.getAccessTokenDirect()
                }

                // Only refresh if token hasn't changed (means another thread didn't refresh)
                if (newAccessToken == accessToken) {
                    val refreshResult = runBlocking {
                        try {
                            apiService.refreshToken(
                                RefreshTokenRequest(
                                    runBlocking { tokenManager.getRefreshTokenDirect() } ?: ""
                                )
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (refreshResult != null && refreshResult.isSuccessful) {
                        val newToken = refreshResult.body()?.accessToken
                        if (newToken != null) {
                            runBlocking {
                                tokenManager.updateAccessToken(newToken)
                            }
                            Log.d("TokenRefreshInterceptor", "Token refreshed successfully")

                            // Retry original request with new token
                            val retryRequest = originalRequest.newBuilder()
                                .addHeader("Authorization", "Bearer $newToken")
                                .build()
                            response.close()
                            return chain.proceed(retryRequest)
                        }
                    } else {
                        Log.e("TokenRefreshInterceptor", "Failed to refresh token")
                        // Token refresh failed, let the app handle 401
                    }
                }
            }
        }

        return response
    }
}
