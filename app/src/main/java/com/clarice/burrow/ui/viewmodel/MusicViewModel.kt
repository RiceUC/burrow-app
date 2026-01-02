package com.clarice.burrow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clarice.burrow.R
import com.clarice.burrow.ui.model.music.MusicModel
import com.clarice.burrow.utils.AudioPlayerManager
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

class MusicViewModel(private val app: Application) : AndroidViewModel(app) {

    // 10.0.2.2 = host IP dari emulator, ganti ke 192.168.18.28 jika pakai real device
    private val BASE_URL = "http://10.0.2.2:3000"

    val musicList = listOf(
        MusicModel(1, "Pink Noise", "About to sleep", 300, R.drawable.sleep_1, "$BASE_URL/api/music/1/stream"),
        MusicModel(2, "Air Mengalir", "About to sleep", 300, R.drawable.sleep_2, "$BASE_URL/api/music/2/stream"),
        MusicModel(3, "Binaural Beats Theta", "About to sleep", 300, R.drawable.sleep_3, "$BASE_URL/api/music/3/stream"),
        MusicModel(4, "Brown Noise", "While sleeping", 300, R.drawable.sleep_1, "$BASE_URL/api/music/4/stream"),
        MusicModel(5, "Delta Waves Binaural Beats", "While sleeping", 300, R.drawable.sleep_2, "$BASE_URL/api/music/5/stream"),
        MusicModel(6, "Hujan Lebat", "While sleeping", 300, R.drawable.sleep_3, "$BASE_URL/api/music/6/stream"),
    )

    val timerOptions = listOf(10, 20, 30, 40, 50, 60)

    private val _aboutSleepMusic = MutableStateFlow<List<MusicModel>>(emptyList())
    val aboutSleepMusic: StateFlow<List<MusicModel>> = _aboutSleepMusic.asStateFlow()

    private val _whileSleepMusic = MutableStateFlow<List<MusicModel>>(emptyList())
    val whileSleepMusic: StateFlow<List<MusicModel>> = _whileSleepMusic.asStateFlow()

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

    private val _currentMusic = MutableStateFlow<MusicModel?>(null)
    val currentMusic: StateFlow<MusicModel?> = _currentMusic.asStateFlow()

    private var timerJob: Job? = null
    private var playlistIndex = 0

    // =========================
    // USER ACTIONS
    // =========================

    fun selectAboutMusic(music: MusicModel) {
        _aboutSleepMusic.value = _aboutSleepMusic.value.toMutableList().apply {
            if (contains(music)) remove(music) else add(music)
        }
    }

    fun selectWhileMusic(music: MusicModel) {
        _whileSleepMusic.value = _whileSleepMusic.value.toMutableList().apply {
            if (contains(music)) remove(music) else add(music)
        }
    }

    fun setAboutDuration(min: Int) {
        _aboutDuration.value = min
    }

    fun setWhileDuration(min: Int) {
        _whileDuration.value = min
    }

    fun seekTo(sec: Int) {
        _currentSec.value = sec.coerceIn(0, _totalSec.value)
        AudioPlayerManager.seekTo((sec * 1000).toLong())
    }

    fun canPlay(): Boolean {
        return _aboutSleepMusic.value.isNotEmpty() &&
                _whileSleepMusic.value.isNotEmpty() &&
                _aboutDuration.value != null &&
                _whileDuration.value != null
    }

    fun startPlayer() {
        if (!canPlay()) {
            android.util.Log.e("MusicViewModel", "Cannot play: aboutMusic=${_aboutSleepMusic.value.size}, whileMusic=${_whileSleepMusic.value.size}, aboutDur=${_aboutDuration.value}, whileDur=${_whileDuration.value}")
            return
        }

        _shouldExitPlayer.value = false
        _phase.value = PlayerPhase.ABOUT_SLEEP
        val aboutDur = _aboutDuration.value
        if (aboutDur != null) {
            startPhase(aboutDur, _aboutSleepMusic.value)
        } else {
            android.util.Log.e("MusicViewModel", "About duration is null")
        }
    }

    // =========================
    // CORE PLAYER LOGIC
    // =========================

    private fun startPhase(durationMin: Int, playlist: List<MusicModel>) {
        if (playlist.isEmpty()) return

        android.util.Log.d("MusicViewModel", "Starting phase with duration=$durationMin min, playlist size=${playlist.size}")

        timerJob?.cancel()
        playlistIndex = 0

        val totalPhaseSec = durationMin * 60
        var elapsedSecInPhase = 0
        var elapsedSecInSong = 0

        _currentMusic.value = playlist[0]
        _totalSec.value = playlist[0].duration
        _currentSec.value = 0
        _isPlaying.value = true

        val audioUrl = playlist[0].audioUrl
        android.util.Log.d("MusicViewModel", "Playing audio from: $audioUrl")
        AudioPlayerManager.playAudio(app, audioUrl)

        timerJob = viewModelScope.launch {
            while (_isPlaying.value && elapsedSecInPhase < totalPhaseSec) {
                delay(1000)
                elapsedSecInPhase++
                elapsedSecInSong++

                _currentSec.value = elapsedSecInSong

                if (elapsedSecInSong >= playlist[playlistIndex].duration) {
                    playlistIndex = (playlistIndex + 1) % playlist.size
                    _currentMusic.value = playlist[playlistIndex]
                    _totalSec.value = playlist[playlistIndex].duration
                    elapsedSecInSong = 0
                    _currentSec.value = 0
                    AudioPlayerManager.playAudio(app, playlist[playlistIndex].audioUrl)
                }
            }

            _isPlaying.value = false

            when (_phase.value) {
                PlayerPhase.ABOUT_SLEEP -> {
                    val whileDur = _whileDuration.value ?: 0
                    if (whileDur > 0) {
                        _phase.value = PlayerPhase.WHILE_SLEEP
                        startPhase(whileDur, _whileSleepMusic.value)
                    } else {
                        _shouldExitPlayer.value = true
                        AudioPlayerManager.pauseAudio()
                    }
                }

                PlayerPhase.WHILE_SLEEP -> {
                    _shouldExitPlayer.value = true
                    AudioPlayerManager.pauseAudio()
                }
            }
        }
    }

    // =========================
    // PLAY / PAUSE
    // =========================

    fun togglePlay() {
        if (_isPlaying.value) pause() else play()
    }

    fun play() {
        if (_isPlaying.value) return
        _isPlaying.value = true
        AudioPlayerManager.resumeAudio()
        resumeTimer()
    }

    fun pause() {
        _isPlaying.value = false
        AudioPlayerManager.pauseAudio()
        timerJob?.cancel()
        timerJob = null
    }

    private fun resumeTimer() {
        if (timerJob != null) return

        val playlist =
            if (_phase.value == PlayerPhase.ABOUT_SLEEP) _aboutSleepMusic.value
            else _whileSleepMusic.value

        val totalPhaseSec =
            if (_phase.value == PlayerPhase.ABOUT_SLEEP) (_aboutDuration.value ?: 0) * 60
            else (_whileDuration.value ?: 0) * 60

        var elapsedSecInPhase = 0
        var elapsedSecInSong = _currentSec.value

        timerJob = viewModelScope.launch {
            while (_isPlaying.value && elapsedSecInPhase < totalPhaseSec) {
                delay(1000)
                elapsedSecInPhase++
                elapsedSecInSong++

                _currentSec.value = elapsedSecInSong

                if (elapsedSecInSong >= playlist[playlistIndex].duration) {
                    playlistIndex = (playlistIndex + 1) % playlist.size
                    _currentMusic.value = playlist[playlistIndex]
                    _totalSec.value = playlist[playlistIndex].duration
                    elapsedSecInSong = 0
                    _currentSec.value = 0
                    AudioPlayerManager.playAudio(app, playlist[playlistIndex].audioUrl)
                }
            }

            // Fase selesai, pindah ke fase berikutnya atau exit
            _isPlaying.value = false

            when (_phase.value) {
                PlayerPhase.ABOUT_SLEEP -> {
                    val whileDur = _whileDuration.value ?: 0
                    if (whileDur > 0) {
                        _phase.value = PlayerPhase.WHILE_SLEEP
                        startPhase(whileDur, _whileSleepMusic.value)
                    } else {
                        _shouldExitPlayer.value = true
                        AudioPlayerManager.pauseAudio()
                    }
                }

                PlayerPhase.WHILE_SLEEP -> {
                    _shouldExitPlayer.value = true
                    AudioPlayerManager.pauseAudio()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        AudioPlayerManager.release()
    }
}