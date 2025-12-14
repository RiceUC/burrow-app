package com.clarice.burrow.ui.model.sleep

import com.google.gson.annotations.SerializedName

data class SleepSession(
    @SerializedName("session_id")
    val sessionId: Int = 0,  // Default to 0 to catch issues

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("start_time")
    val startTime: String,

    @SerializedName("end_time")
    val endTime: String? = null,

    @SerializedName("duration_minutes")
    val durationMinutes: Int? = null,

    @SerializedName("sleep_quality")
    val sleepQuality: Int? = null,

    @SerializedName("created_at")
    val createdAt: String
)