package com.clarice.burrow.ui.model.common

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("data")
    val data: T? = null,

    @SerializedName("message")
    val message: String? = null
)

data class ErrorResponse(
    @SerializedName("errors")
    val errors: String
)