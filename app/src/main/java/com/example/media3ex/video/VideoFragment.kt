package com.example.media3ex.video

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.media3ex.R
import com.example.media3ex.audio.AudioFragment
import com.example.media3ex.databinding.FragmentVideoBinding
import dagger.hilt.android.AndroidEntryPoint

@Suppress("ktlint:standard:backing-property-naming")
@AndroidEntryPoint
class VideoFragment : Fragment() {
    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null
    private var isFullscreen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
        setupCustomControls()
        setupNavigationButton()
    }

    private fun initializePlayer() {
        player =
            ExoPlayer.Builder(requireContext()).build().apply {
                binding.playerView.player = this
                val mediaItem =
                    MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
                setMediaItem(mediaItem)

                prepare()
            }

        binding.playerView.setControllerVisibilityListener(
            PlayerView.ControllerVisibilityListener { visibility ->
                val isVisible = visibility == View.VISIBLE

                // 모든 컨트롤 요소들
                binding.playerView.findViewById<View>(R.id.topGradient)?.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
                binding.playerView.findViewById<View>(R.id.topControls)?.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
                binding.playerView.findViewById<View>(R.id.centerControls)?.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
                binding.playerView.findViewById<View>(R.id.bottomGradient)?.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
                binding.playerView.findViewById<View>(R.id.bottomControls)?.visibility =
                    if (isVisible) View.VISIBLE else View.GONE

                // 얇은 seekbar는 컨트롤러 숨김 시에만 표시
                binding.playerView.findViewById<View>(R.id.thinSeekbar)?.visibility =
                    if (isVisible) View.GONE else View.VISIBLE
            },
        )
    }

    private fun setupCustomControls() {
        // PlayerView의 컨트롤러 뷰 가져오기
        val controllerView = binding.playerView.findViewById<View>(R.id.btnRepeatMode)

        controllerView?.setOnClickListener {
            player?.let {
                it.repeatMode =
                    when (it.repeatMode) {
                        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
                        Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
                        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF
                        else -> Player.REPEAT_MODE_OFF
                    }
            }
        }

        // 재생 속도 버튼
        val speedButton = binding.playerView.findViewById<View>(R.id.btnPlaybackSpeed)
        speedButton?.setOnClickListener {
            showSpeedDialog()
        }

        // 전체화면 버튼
        val fullscreenButton = binding.playerView.findViewById<View>(R.id.btnFullscreen)
        fullscreenButton?.setOnClickListener {
            toggleFullscreen()
        }
    }

    private fun setupNavigationButton() {
        binding.btnAudio.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, AudioFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showSpeedDialog() {
        val speeds = arrayOf("0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "2.0x")
        AlertDialog
            .Builder(requireContext())
            .setTitle("재생 속도")
            .setItems(speeds) { _, which ->
                val speed =
                    when (which) {
                        0 -> 0.5f
                        1 -> 0.75f
                        2 -> 1.0f
                        3 -> 1.25f
                        4 -> 1.5f
                        5 -> 2.0f
                        else -> 1.0f
                    }
                player?.setPlaybackSpeed(speed)
            }.show()
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen()
        } else {
            enterFullscreen()
        }
    }

    private fun enterFullscreen() {
        isFullscreen = true

        activity?.let { act ->
            // 시스템바 숨기기
            WindowCompat.setDecorFitsSystemWindows(act.window, false)
            WindowInsetsControllerCompat(act.window, binding.root).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            // 화면 가로 모드
            act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            act.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        // PlayerView 전체화면
        val params = binding.playerView.layoutParams as ConstraintLayout.LayoutParams
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.topMargin = 0
        params.bottomMargin = 0
        binding.playerView.layoutParams = params

        // 버튼 숨기기
        binding.btnAudio.visibility = View.GONE

        // 아이콘 변경
        binding.playerView
            .findViewById<ImageButton>(R.id.btnFullscreen)
            ?.setImageResource(R.drawable.ic_small_screen)
    }

    private fun exitFullscreen() {
        isFullscreen = false

        activity?.let { act ->
            // 시스템바 보이기
            WindowCompat.setDecorFitsSystemWindows(act.window, true)
            WindowInsetsControllerCompat(act.window, binding.root).apply {
                show(WindowInsetsCompat.Type.systemBars())
            }

            // 화면 세로 모드
            act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            act.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        // PlayerView 원래 크기로 복원
        val params = binding.playerView.layoutParams as ConstraintLayout.LayoutParams
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = 0 // WRAP_CONTENT가 아닌 0dp (match_constraint)
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToTop = binding.btnAudio.id
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        params.topMargin = 0
        params.bottomMargin = 0
        binding.playerView.layoutParams = params

        // 버튼 보이기
        binding.btnAudio.visibility = View.VISIBLE

        // 아이콘 변경
        binding.playerView
            .findViewById<ImageButton>(R.id.btnFullscreen)
            ?.setImageResource(R.drawable.ic_full_screen)
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isFullscreen) {
            exitFullscreen()
        }
        releasePlayer()
        _binding = null
    }
}
