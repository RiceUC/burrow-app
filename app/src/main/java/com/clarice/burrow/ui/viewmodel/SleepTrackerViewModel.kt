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
import com.clarice.burrow.utils.ReminderScheduler
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.Instant // to parse UTC time
import java.time.ZoneId // to convert to local timezone

/**
 * Sleep Session States
 */
enum class SleepSessionState {
    IDLE,       // No session - can start
    ACTIVE,     // Session started - can end
    COMPLETED   // Session ended - can reset
}

/**
 * SleepTrackerViewModel - Handles sleep tracking operations with state management
 */
class SleepTrackerViewModel(context: Context) : ViewModel() {

    private val apiService = RetrofitClient.getApiService(context)
    private val sleepRepository = SleepRepository(apiService)
    private val reminderScheduler = ReminderScheduler(context)

    var sleepState by mutableStateOf(SleepTrackerState())
        private set

    init {
        loadSleepSessions()
        checkActiveSession()
    }

    // ==================== STATE MANAGEMENT ====================

    /**
     * Get current session state
     */
    private fun getCurrentState(): SleepSessionState {
        val session = sleepState.currentSession
        return when {
            session == null -> SleepSessionState.IDLE
            session.endTime == null || session.endTime.isBlank() -> SleepSessionState.ACTIVE
            else -> SleepSessionState.COMPLETED
        }
    }

    // ==================== START SLEEP SESSION ====================

    /**
     * Start a new sleep session
     */
    fun startSleepSession(onSuccess: () -> Unit = {}) {
        val currentState = getCurrentState()
        android.util.Log.d("SleepTracker", "startSleepSession called, current state: $currentState")

        if (currentState != SleepSessionState.IDLE) {
            sleepState = sleepState.copy(error = "Cannot start: session already exists")
            return
        }

        sleepState = sleepState.copy(isStarting = true, error = null)
        val startTime = LocalDateTime.now()

        viewModelScope.launch {
            val result = sleepRepository.startSleepSession(startTime)

            when (result) {
                is NetworkResult.Success -> {
                    val session = result.data?.data
                    android.util.Log.d("SleepTracker", "Session started successfully: $session")
                    sleepState = sleepState.copy(
                        isStarting = false,
                        currentSession = session,
                        error = null
                    )
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("SleepTracker", "Failed to start session: ${result.message}")
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
        onSuccess: () -> Unit = {}
    ) {
        val currentState = getCurrentState()
        android.util.Log.d("SleepTracker", "endSleepSession called, current state: $currentState")
        android.util.Log.d("SleepTracker", "Current session: ${sleepState.currentSession}")

        if (currentState != SleepSessionState.ACTIVE) {
            sleepState = sleepState.copy(error = "Cannot end: no active session")
            return
        }

        val sessionId = sleepState.currentSession?.sessionId
        android.util.Log.d("SleepTracker", "Session ID to end: $sessionId")

        if (sessionId == null || sessionId == 0) {
            sleepState = sleepState.copy(error = "Invalid session ID")
            android.util.Log.e("SleepTracker", "Invalid session ID: $sessionId")
            return
        }

        sleepState = sleepState.copy(isEnding = true, error = null)
        val endTime = LocalDateTime.now()

        viewModelScope.launch {
            val result = sleepRepository.endSleepSession(
                sessionId = sessionId,
                endTime = endTime,
                sleepQuality = sleepQuality
            )

            when (result) {
                is NetworkResult.Success -> {
                    val completedSession = result.data?.data
                    android.util.Log.d("SleepTracker", "Session ended successfully: $completedSession")
                    sleepState = sleepState.copy(
                        isEnding = false,
                        currentSession = completedSession,
                        error = null
                    )
                    // Reload sessions to update history
                    loadSleepSessions()
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("SleepTracker", "Failed to end session: ${result.message}")
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

    // ==================== RESET SESSION ====================

    /**
     * Reset/clear the completed session and return to IDLE state
     */
    fun resetSession() {
        val currentState = getCurrentState()
        android.util.Log.d("SleepTracker", "resetSession called, current state: $currentState")

        if (currentState != SleepSessionState.COMPLETED) {
            sleepState = sleepState.copy(error = "Cannot reset: session not completed")
            return
        }

        sleepState = sleepState.copy(
            currentSession = null,
            error = null
        )

        android.util.Log.d("SleepTracker", "Session reset, new state: ${getCurrentState()}")
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
                    // If we deleted the current session, clear it
                    if (sleepState.currentSession?.sessionId == sessionId) {
                        sleepState = sleepState.copy(currentSession = null)
                    }
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

    // ==================== REMINDER MANAGEMENT ====================

    /**
     * Update reminder enabled state
     */
    fun setReminderEnabled(enabled: Boolean) {
        sleepState = sleepState.copy(isReminderEnabled = enabled)

        if (enabled) {
            scheduleReminder(sleepState.reminderTime)
        } else {
            cancelReminder()
        }
    }

    /**
     * Update reminder time
     */
    fun setReminderTime(time: LocalTime) {
        sleepState = sleepState.copy(reminderTime = time)

        if (sleepState.isReminderEnabled) {
            scheduleReminder(time)
        }
    }

    /**
     * Schedule reminder notification
     */
    private fun scheduleReminder(time: LocalTime) {
        reminderScheduler.scheduleReminder(time)
    }

    /**
     * Cancel reminder notification
     */
    private fun cancelReminder() {
        reminderScheduler.cancelReminder()
    }

    // ==================== HELPERS ====================

    /**
     * Check if there's an active or completed session
     * THIS IS THE KEY FIX - Only set currentSession if it's truly ACTIVE (no end_time)
     */
    private fun checkActiveSession() {
        viewModelScope.launch {
            val result = sleepRepository.getAllSleepSessions()

            if (result is NetworkResult.Success) {
                val sessions = result.data?.data ?: emptyList()

                // FIXED: Only look for sessions without an end_time (truly active)
                val activeSession = sessions.firstOrNull { session ->
                    session.endTime == null || session.endTime.isBlank()
                }

                android.util.Log.d("SleepTracker", "Active session found: $activeSession")

                if (activeSession != null) {
                    sleepState = sleepState.copy(currentSession = activeSession)
                } else {
                    // No active session - ensure we're in IDLE state
                    sleepState = sleepState.copy(currentSession = null)
                }

                android.util.Log.d("SleepTracker", "After check - Button text: ${getButtonText()}")
            }
        }
    }

    /**
     * Get button text based on current state
     */
    fun getButtonText(): String {
        val state = getCurrentState()
        val text = when (state) {
            SleepSessionState.IDLE -> "Start"
            SleepSessionState.ACTIVE -> "End"
            SleepSessionState.COMPLETED -> "Reset"
        }
        android.util.Log.d("SleepTracker", "getButtonText: state=$state, text=$text")
        return text
    }

    /**
     * Get start time display
     */
    fun getStartTimeDisplay(): String {
        val startTime = sleepState.currentSession?.startTime
        return if (startTime != null && startTime.isNotBlank()) {
            formatTime(startTime)
        } else {
            "--:--"
        }
    }

    /**
     * Get end time display
     */
    fun getEndTimeDisplay(): String {
        val endTime = sleepState.currentSession?.endTime
        return if (endTime != null && endTime.isNotBlank()) {
            formatTime(endTime)
        } else {
            "--:--"
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        sleepState = sleepState.copy(error = null)
    }

    /**
     * Format duration - delegate to repository
     */
    fun formatDuration(minutes: Int?): String {
        return sleepRepository.formatDuration(minutes)
    }

    /**
     * Get quality emoji - delegate to repository
     */
    fun getQualityEmoji(quality: Int?): String {
        return sleepRepository.getQualityEmoji(quality)
    }

    /**
     * Format time helper
     * FIXED: Convert UTC time from backend to local timezone
     */
    private fun formatTime(isoDateTime: String): String {
        return try {
            // Parse as Instant (UTC time from backend)
            val instant = Instant.parse(isoDateTime)

            // Convert to local timezone
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

            String.format(Locale.getDefault(), "%02d:%02d", localDateTime.hour, localDateTime.minute)
        } catch (e: Exception) {
            android.util.Log.e("SleepTracker", "Error formatting time: $isoDateTime", e)
            "--:--"
        }
    }
}

/**
 * Sleep Tracker UI State
 */
data class SleepTrackerState(
    val sessions: List<SleepSession> = emptyList(),
    val currentSession: SleepSession? = null,
    val reminderTime: LocalTime = LocalTime.of(21, 30),
    val isReminderEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isStarting: Boolean = false,
    val isEnding: Boolean = false,
    val error: String? = null
)