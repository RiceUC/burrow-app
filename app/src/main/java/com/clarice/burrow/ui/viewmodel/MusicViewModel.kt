package com.clarice.burrow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.R
import com.clarice.burrow.data.repository.MusicRepository
import com.clarice.burrow.ui.model.music.MusicModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class PlayerPhase {
    ABOUT_SLEEP,
    WHILE_SLEEP
}

class MusicViewModel : ViewModel() {

    private val repository = MusicRepository()

    // ================= MUSIC DATA =================

    private val _musicList = MutableStateFlow<List<MusicModel>>(emptyList())
    val musicList: StateFlow<List<MusicModel>> = _musicList.asStateFlow()

    private fun loadStaticMusic() {
        _musicList.value = listOf(
            MusicModel(1, "Night Island", "About to sleep", 30, R.drawable.sleep_1),
            MusicModel(2, "Moon Garden", "About to sleep", 30, R.drawable.sleep_2),
            MusicModel(3, "Owl Dream", "About to sleep", 30, R.drawable.sleep_3),

            MusicModel(4, "Deep Night Owl", "While sleeping", 60, R.drawable.sleep_1),
            MusicModel(5, "Galaxy Dream", "While sleeping", 60, R.drawable.sleep_2),
            MusicModel(6, "Calm Moon", "While sleeping", 60, R.drawable.sleep_3)
        )
    }

    // ================= PLAYER STATE =================

    val timerOptions = listOf(10, 20, 30, 40, 50, 60)

    private val _aboutSleepMusic = MutableStateFlow<MusicModel?>(null)
    val aboutSleepMusic = _aboutSleepMusic.asStateFlow()

    private val _whileSleepMusic = MutableStateFlow<MusicModel?>(null)
    val whileSleepMusic = _whileSleepMusic.asStateFlow()

    private val _aboutDuration = MutableStateFlow<Int?>(null)
    val aboutDuration = _aboutDuration.asStateFlow()

    private val _whileDuration = MutableStateFlow<Int?>(null)
    val whileDuration = _whileDuration.asStateFlow()

    private val _phase = MutableStateFlow(PlayerPhase.ABOUT_SLEEP)
    val phase = _phase.asStateFlow()

    private val _currentSec = MutableStateFlow(0)
    val currentSec = _currentSec.asStateFlow()

    private val _totalSec = MutableStateFlow(0)
    val totalSec = _totalSec.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _shouldExitPlayer = MutableStateFlow(false)
    val shouldExitPlayer = _shouldExitPlayer.asStateFlow()

    private var timerJob: Job? = null

    // ================= ACTION =================

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

    fun startPlayer() {
        if (!canPlay()) return

        _shouldExitPlayer.value = false
        _phase.value = PlayerPhase.ABOUT_SLEEP
        startPhase(_aboutDuration.value!!)
    }

    fun canPlay(): Boolean =
        _aboutSleepMusic.value != null &&
                _whileSleepMusic.value != null &&
                _aboutDuration.value != null &&
                _whileDuration.value != null

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
                }
            }
        }
    }

    fun getCurrentMusic(): MusicModel? =
        if (_phase.value == PlayerPhase.ABOUT_SLEEP)
            _aboutSleepMusic.value
        else
            _whileSleepMusic.value

    fun togglePlay() {
        _isPlaying.value = !_isPlaying.value
    }

    fun seekTo(sec: Int) {
        val validSec = sec.coerceIn(0, _totalSec.value)
        _currentSec.value = validSec
    }

    init {
        loadStaticMusic()
    }
}
