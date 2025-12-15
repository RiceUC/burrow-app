package com.clarice.burrow.ui.model.state

import com.clarice.burrow.ui.model.journal.Journal

sealed class JournalUiState {
    object Idle : JournalUiState()
    object Loading : JournalUiState()
    data class Success(val journals: List<Journal>) : JournalUiState()
    data class Error(val message: String) : JournalUiState()
}