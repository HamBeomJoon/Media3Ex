package com.example.media3ex

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.media3ex.databinding.ViewAudioControlBinding

class AudioControlView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private val binding: ViewAudioControlBinding =
            ViewAudioControlBinding.inflate(LayoutInflater.from(context), this, true)
        private var player: ExoPlayer? = null

        @OptIn(UnstableApi::class)
        fun setPlayer(exoPlayer: ExoPlayer) {
            this.player = exoPlayer
            binding.playerView.player = exoPlayer
            binding.playerView.controllerShowTimeoutMs = 0

            setupCustomControls()
            setupPlayerListener()
        }

        private fun setupCustomControls() {
            binding.playerView.findViewById<View>(R.id.custom_play_pause)?.setOnClickListener {
                player?.let {
                    if (it.isPlaying) {
                        it.pause()
                    } else {
                        it.play()
                    }
                }
            }
        }

        private fun setupPlayerListener() {
            player?.addListener(
                object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        updatePlayPauseIcon(isPlaying)
                    }
                },
            )
        }

        private fun updatePlayPauseIcon(isPlaying: Boolean) {
            val playPauseButton = binding.playerView.findViewById<ImageView>(R.id.custom_play_pause)
            playPauseButton?.setImageResource(
                if (isPlaying) R.drawable.ic_pause_purple else R.drawable.ic_play_purple,
            )
        }

        fun release() {
            player?.release()
            player = null
            binding.playerView.player = null
        }
    }
