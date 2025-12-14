package com.clarice.burrow.ui.model.auth

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerializedName("user_id")
    val userId: Int,
    val username: String,
    val name: String,
    val birthdate: String? = null,
    @SerializedName("default_sound_duration")
    val defaultSoundDuration: Int? = null,
    @SerializedName("reminder_time")
    val reminderTime: String? = null,
    val gender: String? = null,
    @SerializedName("created_at")
    val createdAt: String
)