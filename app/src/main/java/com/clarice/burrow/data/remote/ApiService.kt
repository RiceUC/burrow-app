// File: app/src/main/java/com/clarice/burrow/data/remote/ApiService.kt
package com.clarice.burrow.data.remote

import com.clarice.burrow.ui.model.auth.RegisterRequest
import com.clarice.burrow.ui.model.auth.LoginRequest
import com.clarice.burrow.ui.model.auth.UpdateProfileRequest
import com.clarice.burrow.ui.model.auth.*
import com.clarice.burrow.ui.model.common.ApiResponse
import com.clarice.burrow.ui.model.sleep.SleepSession
<<<<<<< HEAD
import com.clarice.burrow.ui.model.music.MusicResponse
=======
import com.clarice.burrow.ui.model.journal.JournalsResponse
import com.clarice.burrow.ui.model.journal.JournalRequest
import com.clarice.burrow.ui.model.journal.Journal
import com.clarice.burrow.ui.model.journal.JournalSingleResponse
import com.clarice.burrow.ui.model.journal.JournalUpdateRequest
>>>>>>> dbffbdaefdeb84a0278514c38a1c73d9829c520b
import retrofit2.Response
import retrofit2.http.*

// Retrofit API Service (defines all API endpoints)
interface ApiService {

    // ==================== AUTH ENDPOINTS ====================

    @POST("api/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<User>>

    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>


    // USER ENDPOINTS 

    @GET("api/users/profile")
    suspend fun getProfile(): Response<ApiResponse<User>>

    @PUT("api/users/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<User>>

    @DELETE("api/users/account")
    suspend fun deleteAccount(): Response<ApiResponse<String>>


    // ==================== SLEEP ENDPOINTS ====================

    @POST("api/sleep/start")
    suspend fun startSleepSession(
        @Body request: StartSleepRequest
    ): Response<ApiResponse<SleepSession>>

    @PUT("api/sleep/{sessionId}/end")
    suspend fun endSleepSession(
        @Path("sessionId") sessionId: Int,
        @Body request: EndSleepRequest
    ): Response<ApiResponse<SleepSession>>

    @GET("api/sleep/sessions")
    suspend fun getAllSleepSessions(): Response<ApiResponse<List<SleepSession>>>

    @GET("api/sleep/sessions/{sessionId}")
    suspend fun getSleepSession(
        @Path("sessionId") sessionId: Int
    ): Response<ApiResponse<SleepSession>>

    @GET("api/sleep/statistics")
    suspend fun getSleepStatistics(): Response<ApiResponse<SleepStatistics>>

    @DELETE("api/sleep/sessions/{sessionId}")
    suspend fun deleteSleepSession(
        @Path("sessionId") sessionId: Int
    ): Response<ApiResponse<String>>

<<<<<<< HEAD
    // ==================== MUSIC ENDPOINTS ====================
    @GET("api/music")
    suspend fun getAllMusic(): Response<ApiResponse<List<MusicResponse>>>

    @GET("api/music/{id}")
    suspend fun getMusic(@Path("id") id: Int): Response<ApiResponse<MusicResponse>>

    // ==================== JOURNAL ENDPOINTS ====================
    @GET("api/journals")
    suspend fun getAllJournals(): Response<ApiResponse<List<Journal>>>

    @GET("api/journals/{id}")
    suspend fun getJournal(@Path("id") id: Int): Response<ApiResponse<Journal>>

    @POST("api/journals")
    suspend fun createJournal(@Body request: CreateJournalRequest): Response<ApiResponse<Journal>>
=======
    // ==================== JOURNAL ENDPOINTS ====================
    @POST("api/journals")
    suspend fun createJournal(@Body request: JournalRequest): Response<ApiResponse<Journal>>

    @GET("api/journals/single/{id}")
    suspend fun getJournal(@Path("id") journalId: Int): Response<ApiResponse<Journal>>

    @GET("api/journals/{userId}")
    suspend fun getJournals(@Path("userId") userId: Int): Response<ApiResponse<List<Journal>>>
>>>>>>> dbffbdaefdeb84a0278514c38a1c73d9829c520b

    @PUT("api/journals/{id}")
    suspend fun updateJournal(
        @Path("id") id: Int,
<<<<<<< HEAD
        @Body request: UpdateJournalRequest
=======
        @Body request: JournalUpdateRequest
>>>>>>> dbffbdaefdeb84a0278514c38a1c73d9829c520b
    ): Response<ApiResponse<Journal>>

    @DELETE("api/journals/{id}")
    suspend fun deleteJournal(@Path("id") id: Int): Response<ApiResponse<String>>
}

/**
 * Sleep request models
 */
data class StartSleepRequest(
    val start_time: String // ISO 8601 format
)

data class EndSleepRequest(
    val end_time: String, // ISO 8601 format
    val sleep_quality: Int? = null // 1-5
)

/**
 * Sleep Statistics Response Model
 */
data class SleepStatistics(
    val total_sessions: Int,
    val average_duration: Int, // in minutes
    val average_quality: Int,
    val total_sleep_time: Int, // in minutes
    val best_sleep_quality: Int,
    val worst_sleep_quality: Int
)

/**
 * Journal Models
 */
data class Journal(
    val id: Int,
    val user_id: Int,
    val title: String,
    val content: String,
    val mood: String? = null,
    val tags: List<String>? = null,
    val created_at: String,
    val updated_at: String
)

data class CreateJournalRequest(
    val title: String,
    val content: String,
    val mood: String? = null,
    val tags: List<String>? = null
)

data class UpdateJournalRequest(
    val title: String? = null,
    val content: String? = null,
    val mood: String? = null,
    val tags: List<String>? = null
)