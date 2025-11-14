package com.example.viewbindingex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.viewbindingex.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment() {
    private var player: ExoPlayer? = null
    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
    }

    private fun initializePlayer() {
        player =
            ExoPlayer.Builder(requireContext()).build().also {
                binding.playerView.player = it
                val mediaItem =
                    MediaItem.fromUri("asset:///ComposeCoroutineScope.mp3")
                it.setMediaItem(mediaItem)
                it.prepare()
            }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
