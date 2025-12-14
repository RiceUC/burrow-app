package com.clarice.burrow.ui.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val name: String,
    val birthdate: String?,
    val defaultSoundDuration: Int?,
    val reminder_time: String?,
    val gender: String?
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val birthdate: String? = null,
    val defaultSoundDuration: Int? = null,
    val reminderTime: String? = null,
    val gender: String? = null
)