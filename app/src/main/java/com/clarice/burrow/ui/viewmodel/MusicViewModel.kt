package com.clarice.burrow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.data.remote.MusicRepository
import com.clarice.burrow.data.remote.RetrofitClient
import com.clarice.burrow.ui.model.music.MusicCategory
import com.clarice.burrow.ui.model.music.MusicTrack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MusicRepository(RetrofitClient.getApiService(application.applicationContext))

    private val _musicTracks = MutableStateFlow<List<MusicTrack>>(emptyList())
    val musicTracks: StateFlow<List<MusicTrack>> = _musicTracks.asStateFlow()

    private val _selectedTrack = MutableStateFlow<MusicTrack?>(null)
    val selectedTrack: StateFlow<MusicTrack?> = _selectedTrack.asStateFlow()

    // Timer state
    private val _timerRemaining = MutableStateFlow<Long?>(null) // In seconds
    val timerRemaining: StateFlow<Long?> = _timerRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            _musicTracks.value = repository.getMusicList()
        }
    }

    fun selectTrack(track: MusicTrack) {
        _selectedTrack.value = track
        cancelTimer() // Reset timer when changing tracks
    }

    fun clearSelectedTrack() {
        _selectedTrack.value = null
        cancelTimer()
    }

    fun setTimer(minutes: Int) {
        cancelTimer()
        val totalSeconds = minutes * 60L
        _timerRemaining.value = totalSeconds
        _isTimerRunning.value = true

        timerJob = viewModelScope.launch {
            while (_timerRemaining.value!! > 0) {
                delay(1000)
                _timerRemaining.value = _timerRemaining.value!! - 1
            }
            // Timer finished
            stopMusic()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _isTimerRunning.value = false
        _timerRemaining.value = null
    }

    private fun stopMusic() {
        _selectedTrack.value = null
        cancelTimer()
    }
}
