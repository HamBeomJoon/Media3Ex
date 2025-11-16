package com.example.media3ex

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class AudioControlView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private val playerView: PlayerView
        private lateinit var viewModel: AudioControlViewModel

        init {
            LayoutInflater.from(context).inflate(R.layout.view_audio_control, this, true)
            playerView = findViewById(R.id.playerView)
        }

        @OptIn(UnstableApi::class)
        fun setPlayer(
            exoPlayer: ExoPlayer,
            viewModel: AudioControlViewModel,
            lifecycleOwner: LifecycleOwner,
        ) {
            this.viewModel = viewModel

            playerView.player = exoPlayer
            playerView.controllerShowTimeoutMs = 0

            viewModel.setPlayer(exoPlayer)

            playerView.post {
                setupCustomControls(lifecycleOwner)
            }
        }

        private fun setupCustomControls(lifecycleOwner: LifecycleOwner) {
            val playPauseButton = playerView.findViewById<ImageView>(R.id.custom_play_pause)
            val speedText = playerView.findViewById<TextView>(R.id.tvPlaybackSpeed)

            viewModel.isPlaying.observe(lifecycleOwner) { isPlaying ->
                playPauseButton?.setImageResource(
                    if (isPlaying) R.drawable.ic_pause_purple else R.drawable.ic_play_purple,
                )
            }

            viewModel.playbackSpeed.observe(lifecycleOwner) { speed ->
                speedText?.text = speed
            }

            playPauseButton?.setOnClickListener {
                viewModel.togglePlayPause()
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
            radioGroup.check(getRadioIdForSpeedIndex(viewModel.getCurrentSpeedIndex()))

            val dialog =
                AlertDialog
                    .Builder(context)
                    .setView(dialogView)
                    .create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val (speed, index) =
                    when (checkedId) {
                        R.id.speed_075x -> 0.75f to 0
                        R.id.speed_10x -> 1.0f to 1
                        R.id.speed_125x -> 1.25f to 2
                        R.id.speed_15x -> 1.5f to 3
                        R.id.speed_20x -> 2.0f to 4
                        else -> 1.0f to 1
                    }
                viewModel.setPlaybackSpeed(speed, index)
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun getRadioIdForSpeedIndex(index: Int): Int =
            when (index) {
                0 -> R.id.speed_075x
                1 -> R.id.speed_10x
                2 -> R.id.speed_125x
                3 -> R.id.speed_15x
                4 -> R.id.speed_20x
                else -> R.id.speed_10x
            }

        fun release() {
            viewModel.release()
            playerView.player = null
        }
    }
