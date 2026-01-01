package com.clarice.burrow.data.remote

import android.content.Context
import com.clarice.burrow.BurrowApplication

/**
 * ApiClient - Provides global access to ApiService instance
 */
object ApiClient {
    val apiService: ApiService
        get() = RetrofitClient.getApiService(BurrowApplication.appContext)
}
