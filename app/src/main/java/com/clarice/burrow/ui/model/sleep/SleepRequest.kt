package com.clarice.burrow.ui.model.sleep

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSleepSessionRequest(
    @SerialName("start_time")
    val startTime: String,
    @SerialName("end_time")
    val endTime: String,
    @SerialName("duration_minutes")
    val durationMinutes: Int? = null,
    @SerialName("sleep_quality")
    val sleepQuality: Int? = null
)