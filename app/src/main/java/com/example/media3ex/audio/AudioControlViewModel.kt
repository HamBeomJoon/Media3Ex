package com.example.media3ex.audio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// AudioControlViewModel: UI 상태만 관리
class AudioControlViewModel : ViewModel() {
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _playbackSpeed = MutableLiveData("1.0x")
    val playbackSpeed: LiveData<String> = _playbackSpeed

    fun updatePlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun updatePlaybackSpeed(
        speed: String,
        index: Int,
    ) {
        _playbackSpeed.value = speed
    }
}
