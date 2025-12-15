package com.clarice.burrow.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.data.remote.JournalRepository
import com.clarice.burrow.ui.model.journal.Journal
import com.clarice.burrow.ui.model.journal.JournalRequest
import com.clarice.burrow.ui.model.journal.MoodType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

// ================= UI STATE =================

sealed interface JournalUiState {
    object Idle : JournalUiState
    object Loading : JournalUiState
    data class Success(val journals: List<Journal>) : JournalUiState
    data class Error(val message: String) : JournalUiState
}

// ================= VIEWMODEL =================

class JournalViewModel(
    private val journalRepository: JournalRepository = JournalRepository()
) : ViewModel() {

    // 🔹 List state
    private val _uiState = MutableStateFlow<JournalUiState>(JournalUiState.Idle)
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    // 🔹 Single journal state (edit/detail)
    private val _currentJournal = MutableStateFlow<Journal?>(null)
    val currentJournal: StateFlow<Journal?> = _currentJournal.asStateFlow()

    // ================= ACTIONS =================

    fun fetchJournals(userId: Int) = viewModelScope.launch {
        _uiState.value = JournalUiState.Loading

        journalRepository.getJournals(userId)
            .onSuccess { response ->
                _uiState.value =
                    JournalUiState.Success(response.data.orEmpty())
            }
            .onFailure { e ->
                _uiState.value =
                    JournalUiState.Error(e.message ?: "Failed to load journals")
            }
    }

    fun loadJournal(journalId: Int) = viewModelScope.launch {
        _currentJournal.value = null

        journalRepository.getJournal(journalId)
            .onSuccess { journal ->
                _currentJournal.value = journal
            }
            .onFailure { e ->
                Log.e("JournalVM", "Load failed: ${e.message}")
                _currentJournal.value = null
            }
    }

    fun addJournal(
        userId: Int,
        content: String,
        mood: MoodType
    ) = viewModelScope.launch {

        val request = JournalRequest(
            userId = userId,
            content = content,
            mood = mood.name,
            date = LocalDate.now().toString()
        )

        journalRepository.createJournal(request)
            .onSuccess {
                fetchJournals(userId)
            }
            .onFailure { e ->
                _uiState.value =
                    JournalUiState.Error(e.message ?: "Failed to save journal")
            }
    }

    fun updateJournal(
        journalId: Int,
        userId: Int,
        content: String,
        mood: MoodType
    ) = viewModelScope.launch {

        val request = JournalRequest(
            userId = userId,
            content = content,
            mood = mood.name,
            date = LocalDate.now().toString()
        )

        journalRepository.updateJournal(journalId, request)
            .onSuccess {
                fetchJournals(userId)
            }
            .onFailure { e ->
                _uiState.value =
                    JournalUiState.Error(e.message ?: "Failed to update journal")
            }
    }

    fun deleteJournal(journalId: Int, userId: Int) =
        viewModelScope.launch {

            journalRepository.deleteJournal(journalId)
                .onSuccess {
                    fetchJournals(userId)
                }
                .onFailure { e ->
                    _uiState.value =
                        JournalUiState.Error(e.message ?: "Failed to delete journal")
                }
        }
}
