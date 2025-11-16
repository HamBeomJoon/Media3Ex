package com.example.media3ex

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
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
            ViewAudioControlBinding.inflate(
                LayoutInflater.from(context),
                this,
            )
        private var player: ExoPlayer? = null

        fun setPlayer(player: ExoPlayer) {
            this.player = player
            binding.playerView.player = player
        }

        fun release() {
            player?.release()
            player = null
        }
    }
