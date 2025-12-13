package com.clarice.burrow.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.data.remote.NetworkResult
import com.clarice.burrow.data.remote.RetrofitClient
import com.clarice.burrow.data.repository.SleepRepository
import com.clarice.burrow.ui.model.sleep.SleepSession
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * SleepTrackerViewModel - Handles sleep tracking operations
 */
class SleepTrackerViewModel(context: Context) : ViewModel() {

    private val apiService = RetrofitClient.getApiService(context)
    private val sleepRepository = SleepRepository(apiService)

    var sleepState by mutableStateOf(SleepTrackerState())
        private set

    init {
        loadSleepSessions()
        checkActiveSession()
    }

    // ==================== START SLEEP SESSION ====================

    /**
     * Start a new sleep session
     */
    fun startSleepSession(
        startTime: LocalDateTime = LocalDateTime.now(),
        onSuccess: () -> Unit
    ) {
        sleepState = sleepState.copy(isStarting = true, error = null)

        viewModelScope.launch {
            val result = sleepRepository.startSleepSession(startTime)

            when (result) {
                is NetworkResult.Success -> {
                    sleepState = sleepState.copy(
                        isStarting = false,
                        activeSession = result.data?.data,
                        error = null
                    )
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    sleepState = sleepState.copy(
                        isStarting = false,
                        error = result.message ?: "Failed to start sleep session"
                    )
                }
                is NetworkResult.Loading -> {
                    sleepState = sleepState.copy(isStarting = true)
                }
            }
        }
    }

    // ==================== END SLEEP SESSION ====================

    /**
     * End the active sleep session
     */
    fun endSleepSession(
        sleepQuality: Int? = null,
        endTime: LocalDateTime = LocalDateTime.now(),
        onSuccess: () -> Unit
    ) {
        val sessionId = sleepState.activeSession?.sessionId
        if (sessionId == null) {
            sleepState = sleepState.copy(error = "No active session to end")
            return
        }

        sleepState = sleepState.copy(isEnding = true, error = null)

        viewModelScope.launch {
            val result = sleepRepository.endSleepSession(
                sessionId = sessionId,
                endTime = endTime,
                sleepQuality = sleepQuality
            )

            when (result) {
                is NetworkResult.Success -> {
                    sleepState = sleepState.copy(
                        isEnding = false,
                        activeSession = null,
                        lastCompletedSession = result.data?.data,
                        error = null
                    )
                    loadSleepSessions() // Refresh the list
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    sleepState = sleepState.copy(
                        isEnding = false,
                        error = result.message ?: "Failed to end sleep session"
                    )
                }
                is NetworkResult.Loading -> {
                    sleepState = sleepState.copy(isEnding = true)
                }
            }
        }
    }

    // ==================== LOAD SLEEP SESSIONS ====================

    /**
     * Load all sleep sessions
     */
    fun loadSleepSessions() {
        sleepState = sleepState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = sleepRepository.getAllSleepSessions()

            when (result) {
                is NetworkResult.Success -> {
                    val sessions = result.data?.data ?: emptyList()
                    sleepState = sleepState.copy(
                        isLoading = false,
                        sessions = sessions,
                        error = null
                    )
                }
                is NetworkResult.Error -> {
                    sleepState = sleepState.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to load sleep sessions"
                    )
                }
                is NetworkResult.Loading -> {
                    sleepState = sleepState.copy(isLoading = true)
                }
            }
        }
    }

    // ==================== DELETE SESSION ====================

    /**
     * Delete a sleep session
     */
    fun deleteSleepSession(sessionId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = sleepRepository.deleteSleepSession(sessionId)

            when (result) {
                is NetworkResult.Success -> {
                    loadSleepSessions() // Refresh the list
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    sleepState = sleepState.copy(
                        error = result.message ?: "Failed to delete session"
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    // ==================== HELPERS ====================

    /**
     * Check if there's an active session (end_time is null)
     */
    private fun checkActiveSession() {
        viewModelScope.launch {
            val result = sleepRepository.getAllSleepSessions()

            if (result is NetworkResult.Success) {
                val sessions = result.data?.data ?: emptyList()
                val active = sessions.firstOrNull { it.endTime == null }
                sleepState = sleepState.copy(activeSession = active)
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        sleepState = sleepState.copy(error = null)
    }

    /**
     * Format duration helper
     */
    fun formatDuration(minutes: Int?): String {
        return sleepRepository.formatDuration(minutes)
    }

    /**
     * Get quality emoji helper
     */
    fun getQualityEmoji(quality: Int?): String {
        return sleepRepository.getQualityEmoji(quality)
    }
}

/**
 * Sleep Tracker UI State
 */
data class SleepTrackerState(
    val sessions: List<SleepSession> = emptyList(),
    val activeSession: SleepSession? = null,
    val lastCompletedSession: SleepSession? = null,
    val isLoading: Boolean = false,
    val isStarting: Boolean = false,
    val isEnding: Boolean = false,
    val error: String? = null
)