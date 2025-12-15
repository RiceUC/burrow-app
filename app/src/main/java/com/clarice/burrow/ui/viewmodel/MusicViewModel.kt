package com.clarice.burrow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.R
import com.clarice.burrow.ui.model.music.MusicModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PlayerPhase {
    ABOUT_SLEEP,
    WHILE_SLEEP
}

class MusicViewModel : ViewModel() {
    val musicList = listOf(
        MusicModel(1, "Night Island", "About to sleep", 0, R.drawable.sleep_1),
        MusicModel(2, "Moon Garden", "About to sleep", 0, R.drawable.sleep_2),
        MusicModel(3, "Cloud Dream", "About to sleep", 0, R.drawable.sleep_3),

        MusicModel(4, "Deep Night Owl", "While sleeping", 0, R.drawable.sleep_1),
        MusicModel(5, "Galaxy Dream", "While sleeping", 0, R.drawable.sleep_2),
        MusicModel(6, "Calm Moon", "While sleeping", 0, R.drawable.sleep_3),
    )

    val timerOptions = listOf(10, 20, 30, 40, 50, 60)

    private val _aboutSleepMusic = MutableStateFlow<MusicModel?>(null)
    val aboutSleepMusic: StateFlow<MusicModel?> = _aboutSleepMusic.asStateFlow()

    private val _whileSleepMusic = MutableStateFlow<MusicModel?>(null)
    val whileSleepMusic: StateFlow<MusicModel?> = _whileSleepMusic.asStateFlow()

    private val _aboutDuration = MutableStateFlow<Int?>(null)
    val aboutDuration: StateFlow<Int?> = _aboutDuration.asStateFlow()

    private val _whileDuration = MutableStateFlow<Int?>(null)
    val whileDuration: StateFlow<Int?> = _whileDuration.asStateFlow()

    private val _phase = MutableStateFlow(PlayerPhase.ABOUT_SLEEP)
    val phase: StateFlow<PlayerPhase> = _phase.asStateFlow()

    private val _currentSec = MutableStateFlow(0)
    val currentSec: StateFlow<Int> = _currentSec.asStateFlow()

    private val _totalSec = MutableStateFlow(0)
    val totalSec: StateFlow<Int> = _totalSec.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _shouldExitPlayer = MutableStateFlow(false)
    val shouldExitPlayer: StateFlow<Boolean> = _shouldExitPlayer.asStateFlow()

    private var timerJob: Job? = null

    fun selectAboutMusic(music: MusicModel) {
        _aboutSleepMusic.value = music
    }

    fun selectWhileMusic(music: MusicModel) {
        _whileSleepMusic.value = music
    }

    fun setAboutDuration(min: Int) {
        _aboutDuration.value = min
    }

    fun setWhileDuration(min: Int) {
        _whileDuration.value = min
    }

    fun seekTo(sec: Int) {
        _currentSec.value = sec.coerceIn(0, _totalSec.value)
    }

    fun canPlay(): Boolean {
        return _aboutSleepMusic.value != null &&
                _whileSleepMusic.value != null &&
                _aboutDuration.value != null &&
                _whileDuration.value != null
    }

    fun startPlayer() {
        if (!canPlay()) return

        _shouldExitPlayer.value = false // 🔥 RESET DI SINI
        _phase.value = PlayerPhase.ABOUT_SLEEP
        startPhase(_aboutDuration.value!!)
    }

    private fun startPhase(durationMin: Int) {
        timerJob?.cancel()

        _currentSec.value = 0
        _totalSec.value = durationMin * 60
        _isPlaying.value = true

        timerJob = viewModelScope.launch {
            while (_isPlaying.value && _currentSec.value < _totalSec.value) {
                delay(1000)
                _currentSec.value++
            }

            if (_currentSec.value >= _totalSec.value) {
                when (_phase.value) {
                    PlayerPhase.ABOUT_SLEEP -> {
                        _phase.value = PlayerPhase.WHILE_SLEEP
                        startPhase(_whileDuration.value ?: 0)
                    }

                    PlayerPhase.WHILE_SLEEP -> {
                        _isPlaying.value = false
                        _shouldExitPlayer.value = true
                    }

                    else -> {}
                }
            }
        }
    }

    fun togglePlay() {
        if (_isPlaying.value) pause() else play()
    }

    fun play() {
        if (_isPlaying.value) return
        _isPlaying.value = true
        resumeTimer()
    }

    fun pause() {
        _isPlaying.value = false
        timerJob?.cancel()
        timerJob = null
    }

    private fun resumeTimer() {
        if (timerJob != null) return

        timerJob = viewModelScope.launch {
            while (_isPlaying.value && _currentSec.value < _totalSec.value) {
                delay(1000)
                _currentSec.value++
            }

            if (_currentSec.value >= _totalSec.value) {
                when (_phase.value) {
                    PlayerPhase.ABOUT_SLEEP -> {
                        _phase.value = PlayerPhase.WHILE_SLEEP
                        startPhase(_whileDuration.value ?: 0)
                    }

                    PlayerPhase.WHILE_SLEEP -> {
                        _isPlaying.value = false
                        _shouldExitPlayer.value = true
                    }
                }
            }
        }
    }

    fun getCurrentMusic(): MusicModel? =
        if (_phase.value == PlayerPhase.ABOUT_SLEEP)
            _aboutSleepMusic.value
        else
            _whileSleepMusic.value

    }