package com.example.viewbindingex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.viewbindingex.databinding.FragmentPlayerControlBinding

@Suppress("ktlint:standard:backing-property-naming")
class PlayerControlFragment : Fragment() {
    private var _binding: FragmentPlayerControlBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlayerControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
        setupControls()
    }

    private fun initializePlayer() {
        val audioAttributes =
            AudioAttributes
                .Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build()

        player =
            ExoPlayer
                .Builder(requireContext())
                .setAudioAttributes(audioAttributes, true)
                .build()
                .apply {
                    binding.playerView.player = this
                    volume = 1.0f

                    // 여러 미디어 아이템 추가 (플레이리스트)
                    val mediaItems =
                        listOf(
                            MediaItem.fromUri("asset:///ComposeCoroutineScope.mp3"),
                            MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"),
                            MediaItem.fromUri("asset:///ComposeCoroutineScope.mp3"),
                        )
                    setMediaItems(mediaItems)

                    // 초기 설정
                    playWhenReady = false // 자동 재생 OFF
                    repeatMode = Player.REPEAT_MODE_OFF // 반복 없음

                    prepare()
                }

        Log.d("Player", "Volume: ${player?.volume}")
        Log.d("Player", "Audio Attributes: ${player?.audioAttributes}")
    }

    private fun setupControls() {
        // playWhenReady 제어
        binding.btnPlayPause.setOnClickListener {
            player?.let {
                it.playWhenReady = !it.playWhenReady
                updatePlayButton(it.playWhenReady)
            }
        }

        // seekTo 제어
        binding.btnForward10.setOnClickListener {
            player?.let {
                val newPosition = it.currentPosition + 10_000
                it.seekTo(newPosition.coerceAtMost(it.duration))
            }
        }

        binding.btnBackward10.setOnClickListener {
            player?.let {
                val newPosition = it.currentPosition - 10_000
                it.seekTo(newPosition.coerceAtLeast(0))
            }
        }

        // 특정 위치로 이동
        binding.btnSeekToStart.setOnClickListener {
            player?.seekTo(0)
        }

        // repeatMode 제어
        binding.btnRepeatMode.setOnClickListener {
            player?.let {
                it.repeatMode =
                    when (it.repeatMode) {
                        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
                        Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
                        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF
                        else -> Player.REPEAT_MODE_OFF
                    }
                updateRepeatModeButton(it.repeatMode)
            }
        }

        // 다음/이전 트랙
        binding.btnNext.setOnClickListener {
            player?.seekToNextMediaItem()
        }

        binding.btnPrevious.setOnClickListener {
            player?.seekToPreviousMediaItem()
        }

        // 특정 트랙으로 이동
        binding.btnSeekToTrack.setOnClickListener {
            player?.seekTo(1, 0) // 2번째 트랙의 처음으로
        }

        // 재생 속도 제어
        binding.btnSpeed.setOnClickListener {
            player?.let {
                val newSpeed =
                    when (it.playbackParameters.speed) {
                        1.0f -> 1.5f
                        1.5f -> 2.0f
                        2.0f -> 0.5f
                        else -> 1.0f
                    }
                it.setPlaybackSpeed(newSpeed)
                binding.btnSpeed.text = "${newSpeed}x"
            }
        }

        // Player 상태 리스너
        player?.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            // 초기 상태 또는 에러
                        }

                        Player.STATE_BUFFERING -> {
                            // 버퍼링 중
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        Player.STATE_READY -> {
                            // 재생 준비 완료
                            binding.progressBar.visibility = View.GONE
                        }

                        Player.STATE_ENDED -> {
                            // 재생 완료
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlayButton(isPlaying)
                }

                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int,
                ) {
                    // 트랙 변경 시
                    binding.tvCurrentTrack.text = "Track: ${player?.currentMediaItemIndex?.plus(1)}"
                }
            },
        )
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        binding.btnPlayPause.text = if (isPlaying) "Pause" else "Play"
    }

    private fun updateRepeatModeButton(repeatMode: Int) {
        binding.btnRepeatMode.text =
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> "Repeat: OFF"
                Player.REPEAT_MODE_ONE -> "Repeat: ONE"
                Player.REPEAT_MODE_ALL -> "Repeat: ALL"
                else -> "Repeat: OFF"
            }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
