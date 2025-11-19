package com.example.media3ex.presentation.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.media3ex.domain.AudioPlayerRepository
import com.example.media3ex.domain.InitializeAudioPlayerUseCase
import com.example.media3ex.domain.PauseAudioUseCase
import com.example.media3ex.domain.PlayAudioUseCase
import com.example.media3ex.domain.SetPlaybackSpeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioControlViewModel
    @Inject
    constructor(
        private val audioPlayerRepository: AudioPlayerRepository,
        private val initializeAudioPlayerUseCase: InitializeAudioPlayerUseCase,
        private val pauseAudioUseCase: PauseAudioUseCase,
        private val playAudioUseCase: PlayAudioUseCase,
        private val setPlaybackSpeedUseCase: SetPlaybackSpeedUseCase,
    ) : ViewModel() {
        private val _isPlaying = MutableStateFlow(false)
        val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

        private val _currentPosition = MutableStateFlow(0L)
        val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

        private val _duration = MutableStateFlow(0L)
        val duration: StateFlow<Long> = _duration.asStateFlow()

        private val _playbackSpeed = MutableStateFlow("1.0x")
        val playbackSpeed: StateFlow<String> = _playbackSpeed.asStateFlow()

        private val playerListener =
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        val player = audioPlayerRepository.getPlayer()
                        _duration.value = player?.duration ?: 0L
                    }
                }
            }

        init {
            // 주기적으로 현재 위치 업데이트
            viewModelScope.launch {
                while (true) {
                    val player = audioPlayerRepository.getPlayer()
                    _currentPosition.value = player?.currentPosition ?: 0L
                    delay(100) // 100ms마다 업데이트
                }
            }
        }

        fun initialize(
            mediaUri: String,
            title: String,
            artist: String,
            artworkUri: String? = null,
            onReady: (Player?) -> Unit,
        ) {
            initializeAudioPlayerUseCase(mediaUri, title, artist, artworkUri) { player ->
                audioPlayerRepository.addListener(playerListener)
                onReady(player)
            }
        }

        fun togglePlayPause() {
            val player = audioPlayerRepository.getPlayer() ?: return
            if (player.isPlaying) {
                pauseAudioUseCase()
            } else {
                playAudioUseCase()
            }
        }

        fun rewind() {
            val player = audioPlayerRepository.getPlayer() ?: return
            val newPosition = (player.currentPosition - 10000).coerceAtLeast(0)
            player.seekTo(newPosition)
        }

        fun fastForward() {
            val player = audioPlayerRepository.getPlayer() ?: return
            val newPosition = (player.currentPosition + 10000).coerceAtMost(player.duration)
            player.seekTo(newPosition)
        }

        fun seekTo(position: Long) {
            val player = audioPlayerRepository.getPlayer() ?: return
            player.seekTo(position)
        }

        fun cyclePlaybackSpeed() {
            val speeds = listOf(0.5f, 1.0f, 1.5f, 2.0f)
            val currentSpeed = _playbackSpeed.value.removeSuffix("x").toFloatOrNull() ?: 1.0f
            val currentIndex = speeds.indexOf(currentSpeed)
            val nextIndex = (currentIndex + 1) % speeds.size
            val nextSpeed = speeds[nextIndex]

            setPlaybackSpeedUseCase(nextSpeed)
            _playbackSpeed.value = "${nextSpeed}x"
        }

        override fun onCleared() {
            audioPlayerRepository.removeListener(playerListener)
            audioPlayerRepository.release()
            super.onCleared()
        }
    }
