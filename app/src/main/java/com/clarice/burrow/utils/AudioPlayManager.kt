package com.clarice.burrow.utils

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

object AudioPlayerManager {
    private var exoPlayer: ExoPlayer? = null
    private const val TAG = "AudioPlayerManager"

    fun initPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                // Add listener untuk debugging
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        Log.e(TAG, "Player error: ${error.message}", error)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_IDLE -> Log.d(TAG, "State: IDLE")
                            Player.STATE_BUFFERING -> Log.d(TAG, "State: BUFFERING")
                            Player.STATE_READY -> Log.d(TAG, "State: READY")
                            Player.STATE_ENDED -> Log.d(TAG, "State: ENDED")
                        }
                    }
                })
            }
        }
        return exoPlayer!!
    }

    fun playAudio(context: Context, audioUrl: String) {
        try {
            Log.d(TAG, "Playing audio: $audioUrl")
            val player = initPlayer(context)
            val mediaItem = MediaItem.fromUri(audioUrl)
            player.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
                Log.d(TAG, "Player prepared and started")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio: ${e.message}", e)
            e.printStackTrace()
        }
    }

    fun pauseAudio() {
        exoPlayer?.pause()
        Log.d(TAG, "Audio paused")
    }

    fun resumeAudio() {
        exoPlayer?.play()
        Log.d(TAG, "Audio resumed")
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        Log.d(TAG, "Seek to: $position")
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        Log.d(TAG, "Player released")
    }

    fun getPlayer(): ExoPlayer? = exoPlayer
}
