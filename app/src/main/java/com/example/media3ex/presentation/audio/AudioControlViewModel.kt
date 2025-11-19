package com.example.media3ex.presentation.audio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import com.example.media3ex.domain.AudioPlayerRepository
import com.example.media3ex.domain.InitializeAudioPlayerUseCase
import com.example.media3ex.domain.PauseAudioUseCase
import com.example.media3ex.domain.PlayAudioUseCase
import com.example.media3ex.domain.SetPlaybackSpeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
        private val _isPlaying = MutableLiveData(false)
        val isPlaying: LiveData<Boolean> = _isPlaying

        private val _playbackSpeed = MutableLiveData("1.0x")
        val playbackSpeed: LiveData<String> = _playbackSpeed

        private val currentSpeedIndex = MutableLiveData(1)

        private val playerListener =
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.postValue(isPlaying)
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

        fun setPlaybackSpeed(
            speed: Float,
            index: Int,
        ) {
            setPlaybackSpeedUseCase(speed)
            _playbackSpeed.value = "${speed}x"
            currentSpeedIndex.value = index
        }

        override fun onCleared() {
            audioPlayerRepository.removeListener(playerListener)
            audioPlayerRepository.release()
            super.onCleared()
        }
    }
