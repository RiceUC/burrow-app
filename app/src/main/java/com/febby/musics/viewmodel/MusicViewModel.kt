package com.febby.musics.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.febby.musics.R
import com.febby.musics.model.MusicModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PlayerPhase {
    ABOUT_SLEEP,
    WHILE_SLEEP,
    FINISHED
}

class MusicViewModel : ViewModel() {
    val musicList = listOf(
        MusicModel(1, "Night Island", "About to sleep", 0, R.drawable.sleep_1),
        MusicModel(2, "Moon Garden", "About to sleep", 0, R.drawable.sleep_2),
        MusicModel(3, "Cloud Dream", "About to sleep", 0, R.drawable.sleep_3),

        MusicModel(4, "Deep Night Owl", "While sleeping", 0, R.drawable.sleep_2),
        MusicModel(5, "Galaxy Dream", "While sleeping", 0, R.drawable.sleep_2),
        MusicModel(6, "Calm Moon", "While sleeping", 0, R.drawable.sleep_2),
    )

    val timerOptions = listOf(10, 20, 30, 40, 50, 60)

    private val _aboutSleepMusic = mutableStateOf<MusicModel?>(null)
    val aboutSleepMusic: State<MusicModel?> = _aboutSleepMusic

    private val _whileSleepMusic = mutableStateOf<MusicModel?>(null)
    val whileSleepMusic: State<MusicModel?> = _whileSleepMusic

    private val _aboutDuration = mutableStateOf<Int?>(null)
    val aboutDuration: State<Int?> = _aboutDuration

    private val _whileDuration = mutableStateOf<Int?>(null)
    val whileDuration: State<Int?> = _whileDuration

    private val _phase = mutableStateOf(PlayerPhase.ABOUT_SLEEP)
    val phase: State<PlayerPhase> = _phase

    private val _currentSec = mutableStateOf(0)
    val currentSec: State<Int> = _currentSec

    private val _totalSec = mutableStateOf(0)
    val totalSec: State<Int> = _totalSec

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

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
                        _phase.value = PlayerPhase.FINISHED
                        _isPlaying.value = false
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
                        _phase.value = PlayerPhase.FINISHED
                        _isPlaying.value = false
                    }
                    else -> {}
                }
            }
        }
    }

    fun getCurrentMusic(): MusicModel? {
        return when (_phase.value) {
            PlayerPhase.ABOUT_SLEEP -> _aboutSleepMusic.value
            PlayerPhase.WHILE_SLEEP -> _whileSleepMusic.value
            else -> null
        }
    }
}