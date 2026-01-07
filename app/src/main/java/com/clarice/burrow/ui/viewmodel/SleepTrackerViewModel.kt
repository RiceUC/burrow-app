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
import com.clarice.burrow.data.repository.UserRepository
import com.clarice.burrow.ui.model.sleep.SleepSession
import com.clarice.burrow.utils.ReminderScheduler
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.Instant
import java.time.ZoneId

enum class SleepSessionState {
    IDLE,
    ACTIVE,
    COMPLETED
}

class SleepTrackerViewModel(context: Context) : ViewModel() {

    private val apiService = RetrofitClient.getApiService(context)
    private val sleepRepository = SleepRepository(apiService)
    private val userRepository = UserRepository(apiService)
    private val reminderScheduler = ReminderScheduler(context)

    var sleepState by mutableStateOf(SleepTrackerState())
        private set

    init {
        loadSleepSessions()
        checkActiveSession()
        loadUserReminderSettings()
    }

    // LOAD USER REMINDER SETTINGS

    // Load reminder settings from backend
    private fun loadUserReminderSettings() {
        viewModelScope.launch {
            val result = userRepository.getProfile()

            when (result) {
                is NetworkResult.Success -> {
                    val user = result.data?.data
                    user?.reminderTime?.let { timeString ->
                        // Parse reminder time from backend (format: "HH:mm")
                        try {
                            val parts = timeString.split(":")
                            if (parts.size == 2) {
                                val hour = parts[0].toInt()
                                val minute = parts[1].toInt()
                                val reminderTime = LocalTime.of(hour, minute)

                                sleepState = sleepState.copy(
                                    reminderTime = reminderTime,
                                    isReminderEnabled = true
                                )

                                // Schedule the reminder
                                scheduleReminder(reminderTime)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("SleepTracker", "Error parsing reminder time: ${e.message}")
                        }
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("SleepTracker", "Failed to load user settings: ${result.message}")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    // STATE MANAGEMENT

    private fun getCurrentState(): SleepSessionState {
        val session = sleepState.currentSession
        return when {
            session == null -> SleepSessionState.IDLE
            session.endTime == null || session.endTime.isBlank() -> SleepSessionState.ACTIVE
            else -> SleepSessionState.COMPLETED
        }
    }

    // START SLEEP SESSION

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

    // END SLEEP SESSION

    fun endSleepSession(sleepQuality: Int? = null, onSuccess: () -> Unit = {}) {
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

    // RESET SESSION

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

    // LOAD SLEEP SESSIONS

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

    // DELETE SESSION

    fun deleteSleepSession(sessionId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = sleepRepository.deleteSleepSession(sessionId)

            when (result) {
                is NetworkResult.Success -> {
                    if (sleepState.currentSession?.sessionId == sessionId) {
                        sleepState = sleepState.copy(currentSession = null)
                    }
                    loadSleepSessions()
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

    // REMINDER MANAGEMENT

    fun setReminderEnabled(enabled: Boolean) {
        sleepState = sleepState.copy(isReminderEnabled = enabled)

        if (enabled) {
            scheduleReminder(sleepState.reminderTime)
            // Save to backend
            updateReminderTimeOnBackend(sleepState.reminderTime)
        } else {
            cancelReminder()
        }
    }

    fun setReminderTime(time: LocalTime) {
        sleepState = sleepState.copy(reminderTime = time)

        if (sleepState.isReminderEnabled) {
            scheduleReminder(time)
            // Save to backend
            updateReminderTimeOnBackend(time)
        }
    }

    private fun scheduleReminder(time: LocalTime) {
        reminderScheduler.scheduleReminder(time)
    }

    private fun cancelReminder() {
        reminderScheduler.cancelReminder()
    }

    // Update reminder time on backend
    private fun updateReminderTimeOnBackend(time: LocalTime) {
        viewModelScope.launch {
            val timeString = String.format(Locale.getDefault(), "%02d:%02d", time.hour, time.minute)

            val result = userRepository.updateProfile(
                name = null,
                birthdate = null,
                defaultSoundDuration = null,
                reminderTime = timeString,
                gender = null
            )

            when (result) {
                is NetworkResult.Success -> {
                    android.util.Log.d("SleepTracker", "Reminder time updated on backend: $timeString")
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("SleepTracker", "Failed to update reminder time: ${result.message}")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    // HELPERS

    private fun checkActiveSession() {
        viewModelScope.launch {
            val result = sleepRepository.getAllSleepSessions()

            if (result is NetworkResult.Success) {
                val sessions = result.data?.data ?: emptyList()

                val activeSession = sessions.firstOrNull { session ->
                    session.endTime == null || session.endTime.isBlank()
                }

                android.util.Log.d("SleepTracker", "Active session found: $activeSession")

                if (activeSession != null) {
                    sleepState = sleepState.copy(currentSession = activeSession)
                } else {
                    sleepState = sleepState.copy(currentSession = null)
                }

                android.util.Log.d("SleepTracker", "After check - Button text: ${getButtonText()}")
            }
        }
    }

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

    fun getStartTimeDisplay(): String {
        val startTime = sleepState.currentSession?.startTime
        return if (startTime != null && startTime.isNotBlank()) {
            formatTime(startTime)
        } else {
            "--:--"
        }
    }

    fun getEndTimeDisplay(): String {
        val endTime = sleepState.currentSession?.endTime
        return if (endTime != null && endTime.isNotBlank()) {
            formatTime(endTime)
        } else {
            "--:--"
        }
    }

    fun clearError() {
        sleepState = sleepState.copy(error = null)
    }

    fun formatDuration(minutes: Int?): String {
        return sleepRepository.formatDuration(minutes)
    }

    fun getQualityEmoji(quality: Int?): String {
        return sleepRepository.getQualityEmoji(quality)
    }

    private fun formatTime(isoDateTime: String): String {
        return try {
            val instant = Instant.parse(isoDateTime)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            String.format(Locale.getDefault(), "%02d:%02d", localDateTime.hour, localDateTime.minute)
        } catch (e: Exception) {
            android.util.Log.e("SleepTracker", "Error formatting time: $isoDateTime", e)
            "--:--"
        }
    }
}

data class SleepTrackerState(
    val sessions: List<SleepSession> = emptyList(),
    val currentSession: SleepSession? = null,
    val reminderTime: LocalTime = LocalTime.of(21, 30),
    val isReminderEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isStarting: Boolean = false,
    val isEnding: Boolean = false,
    val error: String? = null
)