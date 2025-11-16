package com.example.media3ex

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class AudioControlViewModel : ViewModel() {
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _playbackSpeed = MutableLiveData("1.0x")
    val playbackSpeed: LiveData<String> = _playbackSpeed

    private var player: ExoPlayer? = null
    private var currentSpeedIndex = 1

    companion object {
        private val SPEEDS = arrayOf("0.75x", "1.0x", "1.25x", "1.5x", "2.0x")
    }

    fun setPlayer(exoPlayer: ExoPlayer) {
        this.player = exoPlayer
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            _isPlaying.value = player?.isPlaying ?: false
                        }

                        Player.STATE_ENDED -> {
                            _isPlaying.value = false
                        }
                    }
                }
            },
        )
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun setPlaybackSpeed(
        speed: Float,
        index: Int,
    ) {
        player?.setPlaybackSpeed(speed)
        currentSpeedIndex = index
        _playbackSpeed.value = SPEEDS[index]
    }

    fun getCurrentSpeedIndex() = currentSpeedIndex

    override fun onCleared() {
        super.onCleared()
        player = null
    }

    fun release() {
        player = null
    }
}
