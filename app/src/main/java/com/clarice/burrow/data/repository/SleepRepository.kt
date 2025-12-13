package com.clarice.burrow.data.repository

import com.clarice.burrow.data.remote.*
import com.clarice.burrow.ui.model.common.ApiResponse
import com.clarice.burrow.ui.model.sleep.SleepSession
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SleepRepository(
    private val apiService: ApiService
) {

    private val isoFormatter = DateTimeFormatter.ISO_DATE_TIME

    /**
     * Start a new sleep session
     */
    suspend fun startSleepSession(
        startTime: LocalDateTime = LocalDateTime.now()
    ): NetworkResult<ApiResponse<SleepSession>> {
        val request = StartSleepRequest(
            start_time = startTime.format(isoFormatter)
        )

        return safeApiCall { apiService.startSleepSession(request) }
    }

    /**
     * End an active sleep session
     */
    suspend fun endSleepSession(
        sessionId: Int,
        endTime: LocalDateTime = LocalDateTime.now(),
        sleepQuality: Int? = null
    ): NetworkResult<ApiResponse<SleepSession>> {
        val request = EndSleepRequest(
            end_time = endTime.format(isoFormatter),
            sleep_quality = sleepQuality
        )

        return safeApiCall { apiService.endSleepSession(sessionId, request) }
    }

    /**
     * Get all sleep sessions for current user
     */
    suspend fun getAllSleepSessions(): NetworkResult<ApiResponse<List<SleepSession>>> {
        return safeApiCall { apiService.getAllSleepSessions() }
    }

    /**
     * Get specific sleep session
     */
    suspend fun getSleepSession(sessionId: Int): NetworkResult<ApiResponse<SleepSession>> {
        return safeApiCall { apiService.getSleepSession(sessionId) }
    }

    /**
     * Get sleep statistics
     */
    suspend fun getSleepStatistics(): NetworkResult<ApiResponse<SleepStatistics>> {
        return safeApiCall { apiService.getSleepStatistics() }
    }

    /**
     * Delete a sleep session
     */
    suspend fun deleteSleepSession(sessionId: Int): NetworkResult<ApiResponse<String>> {
        return safeApiCall { apiService.deleteSleepSession(sessionId) }
    }

    /**
     * Helper: Format duration in minutes to readable string
     */
    fun formatDuration(minutes: Int?): String {
        if (minutes == null) return "N/A"
        val hours = minutes / 60
        val mins = minutes % 60
        return "${hours}h ${mins}m"
    }

    /**
     * Helper: Get quality emoji
     */
    fun getQualityEmoji(quality: Int?): String {
        return when (quality) {
            5 -> "😴" // Excellent
            4 -> "😊" // Good
            3 -> "😐" // Average
            2 -> "😕" // Poor
            1 -> "😫" // Very Poor
            else -> "❓" // Unknown
        }
    }
}