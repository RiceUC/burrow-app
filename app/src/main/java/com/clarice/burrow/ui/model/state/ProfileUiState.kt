package com.clarice.burrow.ui.model.state

import com.clarice.burrow.ui.model.auth.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)