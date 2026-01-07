package com.clarice.burrow.data.remote

import android.content.Context
import com.clarice.burrow.data.local.TokenManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient - Singleton for Retrofit instance
 */
object RetrofitClient {
    // USE THIS FOR ANDROID EMULATOR (10.0.2.2 maps to your host machine's localhost)
    // private const val BASE_URL = "http://10.0.2.2:3000/"

    // USE THIS FOR PHYSICAL DEVICE ON SAME WIFI
     private const val BASE_URL = "http://10.26.127.70:3000/"
    private var apiService: ApiService? = null

    /**
     * Get ApiService instance
     */
    fun getApiService(context: Context): ApiService {
        if (apiService == null) {
            apiService = createApiService(context)
        }
        return apiService!!
    }

    /**
     * Create Retrofit ApiService
     */
    private fun createApiService(context: Context): ApiService {
        val tokenManager = TokenManager(context.applicationContext)

        // Logging interceptor for debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Auth interceptor for token injection
        val authInterceptor = AuthInterceptor(tokenManager)

        // OkHttp client
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Gson converter
        val gson = GsonBuilder()
            .setLenient()
            .create()

        // Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }

    /**
     * Reset client (useful for testing or logout)
     */
    fun resetClient() {
        apiService = null
    }
}