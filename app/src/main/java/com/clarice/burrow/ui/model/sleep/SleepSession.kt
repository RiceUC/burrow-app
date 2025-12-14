package com.clarice.burrow.ui.model.sleep

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SleepSession(
    @SerialName("session_id")
    val sessionId: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("end_time")
    val endTime: String? = null,
    @SerialName("duration_minutes")
    val durationMinutes: Int? = null,
    @SerialName("sleep_quality")
    val sleepQuality: Int? = null,
    @SerialName("created_at")
    val createdAt: String
)