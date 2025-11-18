package com.example.media3ex.presentation.audio

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.example.media3ex.R

class AudioControlView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private val playerView: PlayerView
        private var player: Player? = null
        var onPlayPauseClick: (() -> Unit)? = null
        var onSpeedChangeClick: ((Float, Int) -> Unit)? = null
        var currentSpeedIndex: Int = 1

        init {
            LayoutInflater.from(context).inflate(R.layout.view_audio_control, this, true)
            playerView = findViewById(R.id.playerView)
        }

        @OptIn(UnstableApi::class)
        fun setPlayer(player: Player) {
            this.player = player
            playerView.player = player
            playerView.controllerShowTimeoutMs = 0

            playerView.post {
                setupCustomControls()
            }
        }

        private fun setupCustomControls() {
            val playPauseButton = playerView.findViewById<ImageView>(R.id.custom_play_pause)
            val speedText = playerView.findViewById<TextView>(R.id.tvPlaybackSpeed)

            playPauseButton?.setOnClickListener {
                onPlayPauseClick?.invoke()
            }

            speedText?.setOnClickListener {
                showSpeedDialog()
            }
        }

        private fun showSpeedDialog() {
            val dialogView =
                LayoutInflater.from(context).inflate(
                    R.layout.dialog_speed_selector,
                    null,
                    true,
                )

            val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupSpeed)
            radioGroup.check(getRadioIdForSpeedIndex(currentSpeedIndex))

            val dialog =
                AlertDialog
                    .Builder(context)
                    .setView(dialogView)
                    .create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val (speed, index) =
                    when (checkedId) {
                        R.id.speed_05x -> 0.5f to 0
                        R.id.speed_075x -> 0.75f to 1
                        R.id.speed_10x -> 1.0f to 2
                        R.id.speed_125x -> 1.25f to 3
                        R.id.speed_15x -> 1.5f to 4
                        R.id.speed_20x -> 2.0f to 5
                        else -> 1.0f to 2
                    }
                currentSpeedIndex = index
                onSpeedChangeClick?.invoke(speed, index)
                dialog.dismiss()
            }

            dialog.show()
        }

        fun updatePlayPauseButton(isPlaying: Boolean) {
            val playPauseButton = playerView.findViewById<ImageView>(R.id.custom_play_pause)
            playPauseButton?.setImageResource(
                if (isPlaying) R.drawable.ic_pause_purple else R.drawable.ic_play_purple,
            )
        }

        fun updateSpeedText(speed: String) {
            val speedText = playerView.findViewById<TextView>(R.id.tvPlaybackSpeed)
            speedText?.text = speed
        }

        private fun getRadioIdForSpeedIndex(index: Int): Int =
            when (index) {
                0 -> R.id.speed_05x
                1 -> R.id.speed_075x
                2 -> R.id.speed_10x
                3 -> R.id.speed_125x
                4 -> R.id.speed_15x
                5 -> R.id.speed_20x
                else -> R.id.speed_10x
            }

        fun release() {
            playerView.player = null
            player = null
        }
    }
