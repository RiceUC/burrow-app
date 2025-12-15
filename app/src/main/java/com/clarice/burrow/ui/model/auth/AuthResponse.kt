package com.clarice.burrow.ui.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

@Serializable
data class RefreshTokenResponse(
    val accessToken: String
)