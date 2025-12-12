package com.clarice.burrow.ui.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id")
    val userId: Int,
    val username: String,
    val name: String,
    val birthdate: String? = null,
    @SerialName("default_sound_duration")
    val defaultSoundDuration: Int? = null,
    @SerialName("reminder_time")
    val reminderTime: String? = null,
    val gender: String? = null,
    @SerialName("created_at")
    val createdAt: String
)