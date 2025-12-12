package com.clarice.burrow.ui.model.common

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class ErrorResponse(
    val errors: String
)