package com.clarice.burrow.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.ui.model.journal.Journal
import com.clarice.burrow.ui.model.journal.JournalRequest
import com.clarice.burrow.ui.model.journal.JournalUpdateRequest
import com.clarice.burrow.ui.model.journal.MoodType
import com.clarice.burrow.data.remote.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class JournalViewModel(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _journals = MutableStateFlow<List<Journal>>(emptyList())
    val journals: StateFlow<List<Journal>> = _journals.asStateFlow()
    private val _currentJournal = MutableStateFlow<Journal?>(null)
    val currentJournal: StateFlow<Journal?> = _currentJournal.asStateFlow()
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()


    fun fetchJournals(userId: Int) {
        viewModelScope.launch {
            journalRepository.getJournals(userId)
                .onSuccess { journals ->
                    _journals.value = journals
                }
                .onFailure { e ->
                    Log.e("JournalVM", "Fetch failed: ${e.message}")
                }
        }
    }

    fun loadJournal(journalId: Int) {
        _currentJournal.value = null

        viewModelScope.launch {
            journalRepository.getJournal(journalId)
                .onSuccess { journal ->
                    _currentJournal.value = journal
                }
                .onFailure { e ->
                    Log.e("JournalVM", "Load failed: ${e.message}")
                }
        }
    }

    fun addJournal(
        userId: Int,
        content: String,
        mood: MoodType,
        onComplete: () -> Unit = {}
    ) {
        _isSaving.value = true
        viewModelScope.launch {
            val request = JournalRequest(
                user_id = userId,
                content = content,
                mood = mood.name.lowercase()
            )

            journalRepository.createJournal(request)
                .onSuccess {
                    android.util.Log.d("JournalVM", "Journal created successfully")
                    fetchJournals(userId)
                    _isSaving.value = false
                    onComplete()
                }
                .onFailure { e ->
                    android.util.Log.e("JournalVM", "Add failed: ${e.message}")
                    _isSaving.value = false
                }
        }
    }

    fun updateJournal(
        journalId: Int,
        userId: Int,
        content: String,
        mood: MoodType,
        onComplete: () -> Unit = {}
    ) {
        _isSaving.value = true
        viewModelScope.launch {
            val request = JournalUpdateRequest(
                content = content,
                mood = mood.name.lowercase()
            )

            journalRepository.updateJournal(journalId, request)
                .onSuccess {
                    android.util.Log.d("JournalVM", "Journal updated successfully")
                    fetchJournals(userId)
                    _isSaving.value = false
                    onComplete()
                }
                .onFailure { e ->
                    android.util.Log.e("JournalVM", "Update failed: ${e.message}")
                    _isSaving.value = false
                }
        }
    }

    fun deleteJournal(journalId: Int, userId: Int) {
        _journals.value = _journals.value.filter { it.journal_id != journalId }
        
        viewModelScope.launch {
            journalRepository.deleteJournal(journalId)
                .onSuccess {
                    android.util.Log.d("JournalVM", "Journal deleted successfully")
                }
                .onFailure { e ->
                    android.util.Log.e("JournalVM", "Delete failed: ${e.message}")
                    // kalo failed bakal reload list to restore item
                    fetchJournals(userId)
                }
        }
    }

    companion object {
        fun Factory(repository: JournalRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return JournalViewModel(repository) as T
                }
            }
        }
    }
}
