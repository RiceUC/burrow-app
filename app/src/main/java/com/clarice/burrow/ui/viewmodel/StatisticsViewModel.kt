package com.clarice.burrow.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.data.remote.NetworkResult
import com.clarice.burrow.data.remote.RetrofitClient
import com.clarice.burrow.data.remote.SleepStatistics
import com.clarice.burrow.data.repository.SleepRepository
import com.clarice.burrow.ui.model.sleep.SleepSession
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * StatisticsViewModel - Handles sleep statistics and data visualization
 */
class StatisticsViewModel(context: Context) : ViewModel() {

    private val apiService = RetrofitClient.getApiService(context)
    private val sleepRepository = SleepRepository(apiService)

    var statisticsState by mutableStateOf(StatisticsState())
        private set

    init {
        loadStatistics()
        loadRecentSessions()
    }

    // ==================== LOAD STATISTICS ====================

    /**
     * Load sleep statistics
     */
    fun loadStatistics() {
        statisticsState = statisticsState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = sleepRepository.getSleepStatistics()

            when (result) {
                is NetworkResult.Success -> {
                    statisticsState = statisticsState.copy(
                        isLoading = false,
                        statistics = result.data?.data,
                        error = null
                    )
                }
                is NetworkResult.Error -> {
                    statisticsState = statisticsState.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to load statistics"
                    )
                }
                is NetworkResult.Loading -> {
                    statisticsState = statisticsState.copy(isLoading = true)
                }
            }
        }
    }

    // ==================== LOAD RECENT SESSIONS ====================

    /**
     * Load recent sleep sessions for chart
     */
    fun loadRecentSessions() {
        viewModelScope.launch {
            val result = sleepRepository.getAllSleepSessions()

            when (result) {
                is NetworkResult.Success -> {
                    val sessions = result.data?.data ?: emptyList()

                    // Get last 30 days of data for chart
                    val recentSessions = sessions
                        .filter { it.endTime != null }
                        .take(30)
                        .sortedByDescending { it.startTime }

                    statisticsState = statisticsState.copy(
                        recentSessions = recentSessions
                    )
                }
                is NetworkResult.Error -> {
                    statisticsState = statisticsState.copy(
                        error = result.message ?: "Failed to load recent sessions"
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    // ==================== PERIOD FILTERING ====================

    /**
     * Update selected period (Week, Month, Year)
     */
    fun updatePeriod(period: StatisticsPeriod) {
        statisticsState = statisticsState.copy(selectedPeriod = period)
        loadStatistics()
    }

    // ==================== HELPERS ====================

    /**
     * Format duration to hours and minutes
     */
    fun formatDuration(minutes: Int?): String {
        return sleepRepository.formatDuration(minutes)
    }

    /**
     * Format duration to hours only (for chart)
     */
    fun formatDurationHours(minutes: Int?): String {
        if (minutes == null) return "0h"
        val hours = minutes / 60.0
        return String.format("%.1fh", hours)
    }

    /**
     * Get quality emoji
     */
    fun getQualityEmoji(quality: Int?): String {
        return sleepRepository.getQualityEmoji(quality)
    }

    /**
     * Calculate average for display
     */
    fun getAverageSleepFormatted(): String {
        val avg = statisticsState.statistics?.average_duration ?: 0
        return formatDuration(avg)
    }

    /**
     * Get date range string based on selected period
     */
    fun getDateRangeString(): String {
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
        val now = LocalDateTime.now()

        return when (statisticsState.selectedPeriod) {
            StatisticsPeriod.WEEK -> {
                val weekAgo = now.minusWeeks(1)
                "${weekAgo.format(formatter)} - ${now.format(formatter)}"
            }
            StatisticsPeriod.MONTH -> {
                val monthAgo = now.minusMonths(1)
                "${monthAgo.format(formatter)} - ${now.format(formatter)}"
            }
            StatisticsPeriod.YEAR -> {
                val yearAgo = now.minusYears(1)
                "${yearAgo.format(formatter)} - ${now.format(formatter)}"
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        statisticsState = statisticsState.copy(error = null)
    }
}

/**
 * Statistics UI State
 */
data class StatisticsState(
    val statistics: SleepStatistics? = null,
    val recentSessions: List<SleepSession> = emptyList(),
    val selectedPeriod: StatisticsPeriod = StatisticsPeriod.WEEK,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Statistics Period enum
 */
enum class StatisticsPeriod {
    WEEK,
    MONTH,
    YEAR
}