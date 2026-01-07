package com.clarice.burrow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.data.remote.MusicRepository
import com.clarice.burrow.data.remote.RetrofitClient
import com.clarice.burrow.ui.model.music.MusicTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MusicRepository(RetrofitClient.getApiService(application.applicationContext))

    private val _musicTracks = MutableStateFlow<List<MusicTrack>>(emptyList())
    val musicTracks: StateFlow<List<MusicTrack>> = _musicTracks.asStateFlow()

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            _musicTracks.value = repository.getMusicList()
        }
    }
}