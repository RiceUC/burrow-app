package com.clarice.burrow.ui.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: User
)