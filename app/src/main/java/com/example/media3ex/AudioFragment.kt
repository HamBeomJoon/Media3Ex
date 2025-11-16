package com.example.media3ex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.media3ex.databinding.FragmentAudioBinding

@Suppress("ktlint:standard:backing-property-naming")
class AudioFragment : Fragment() {
    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
        setupNavigation()
    }

    private fun initializePlayer() {
        player =
            ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
                val mediaItem = MediaItem.fromUri("asset:///ComposeCoroutineScope.mp3")
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()

                // CustomView에 플레이어 설정
                binding.audioControlView.setPlayer(exoPlayer)
            }
    }

    private fun setupNavigation() {
        binding.btnVideo.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, VideoFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        binding.audioControlView.release()
        _binding = null
    }
}
