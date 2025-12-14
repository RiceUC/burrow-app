package com.kiara.journal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.kiara.journal.data.model.Journal
import com.kiara.journal.data.model.JournalRequest
import com.kiara.journal.data.model.MoodType
import com.kiara.journal.data.remote.JournalRepository
import java.time.LocalDate
import android.util.Log


sealed class JournalUiState {
    object Idle : JournalUiState()
    object Loading : JournalUiState()
    data class Success(val journals: List<Journal>) : JournalUiState()
    data class Error(val message: String) : JournalUiState()
}

class JournalViewModel(
    private val journalRepository: JournalRepository = JournalRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<JournalUiState>(JournalUiState.Idle)
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    // 🆕 Store single journal for loading
    private val _currentJournal = MutableStateFlow<Journal?>(null)
    val currentJournal: StateFlow<Journal?> = _currentJournal.asStateFlow()

    fun fetchJournals(userId: Int) {
        viewModelScope.launch {
            _uiState.value = JournalUiState.Loading

            val result = journalRepository.getJournals(userId)

            result.onSuccess { response ->
                val journals = response.data ?: emptyList()
                _uiState.value = JournalUiState.Success(journals)
            }

            result.onFailure { exception ->
                _uiState.value = JournalUiState.Error(exception.message ?: "Failed to load journals")
            }
        }
    }

    // 🆕 NEW: Load single journal for editing
    // 🆕 FIXED: Simplified loadJournal - just trigger the fetch
    fun loadJournal(journalId: Int) {
        viewModelScope.launch {
            try {
                Log.d("JournalViewModel", "loadJournal called with journalId=$journalId")

                val result = journalRepository.getJournal(journalId)

                result.onSuccess { journal ->
                    Log.d("JournalViewModel", "Journal loaded successfully: id=${journal.id}, content=${journal.content}")
                    _currentJournal.value = journal
                }

                result.onFailure { exception ->
                    Log.e("JournalViewModel", "Failed to load journal: ${exception.message}")
                    _currentJournal.value = null
                }
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error loading journal: ${e.message}")
                _currentJournal.value = null
            }
        }
    }


    fun addJournal(content: String, mood: MoodType, userId: Int) {
        viewModelScope.launch {
            try {
                val today = LocalDate.now().toString()

                Log.d("JournalViewModel", "addJournal called with userId=$userId")

                val request = JournalRequest(
                    userId = userId,
                    content = content,
                    mood = mood.name,
                    date = today
                )

                Log.d("JournalViewModel", "Request: $request")

                val result = journalRepository.createJournal(request)

                result.onSuccess {
                    Log.d("JournalViewModel", "Journal created successfully")
                    fetchJournals(userId)
                }

                result.onFailure { exception ->
                    _uiState.value = JournalUiState.Error(exception.message ?: "Failed to save journal")
                }
            } catch (e: Exception) {
                _uiState.value = JournalUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // 🆕 NEW: Update existing journal
    fun updateJournalData(journalId: Int, userId: Int, content: String, mood: MoodType) {
        viewModelScope.launch {
            try {
                Log.d("JournalViewModel", "updateJournal called with id=$journalId, userId=$userId")

                val today = LocalDate.now().toString()

                val request = JournalRequest(
                    userId = userId,
                    content = content,
                    mood = mood.name,
                    date = today
                )

                Log.d("JournalViewModel", "Update Request: $request")

                val result = journalRepository.updateJournal(journalId, request)

                result.onSuccess {
                    Log.d("JournalViewModel", "Journal updated successfully")
                    fetchJournals(userId)
                }

                result.onFailure { exception ->
                    Log.e("JournalViewModel", "Failed to update: ${exception.message}")
                    _uiState.value = JournalUiState.Error(exception.message ?: "Failed to update journal")
                }
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error updating journal: ${e.message}")
                _uiState.value = JournalUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteJournal(id: Int, userId: Int) {
        viewModelScope.launch {
            try {
                val result = journalRepository.deleteJournal(id)

                result.onSuccess {
                    Log.d("JournalViewModel", "Journal deleted successfully")
                    fetchJournals(userId)
                }

                result.onFailure { exception ->
                    _uiState.value = JournalUiState.Error(exception.message ?: "Failed to delete journal")
                }
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error deleting journal: ${e.message}")
            }
        }
    }
}
