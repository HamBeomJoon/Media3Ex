package com.example.media3ex.presentation.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.media3ex.R
import com.example.media3ex.databinding.FragmentAudioBinding
import com.example.media3ex.presentation.video.VideoFragment
import dagger.hilt.android.AndroidEntryPoint

@Suppress("ktlint:standard:backing-property-naming")
@AndroidEntryPoint
class AudioFragment : Fragment() {
    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!
    private val audioControlViewModel: AudioControlViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
        setupNavigation()
        observeViewModel()
    }

    private fun initializePlayer() {
        audioControlViewModel.initialize(
            mediaUri = "asset:///ComposeCoroutineScope.mp3",
            title = "Compose Coroutine Scope",
            artist = "Android Developer",
            artworkUri = null,
        ) { player ->
            player?.let {
                binding.viewAudioControl.setPlayer(it)
                setupViewCallbacks()
            }
        }
    }

    private fun setupViewCallbacks() {
        binding.viewAudioControl.onPlayPauseClick = {
            audioControlViewModel.togglePlayPause()
        }

        binding.viewAudioControl.onSpeedChangeClick = { speed, index ->
            audioControlViewModel.setPlaybackSpeed(speed, index)
        }
    }

    private fun observeViewModel() {
        audioControlViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.viewAudioControl.updatePlayPauseButton(isPlaying)
        }

        audioControlViewModel.playbackSpeed.observe(viewLifecycleOwner) { speed ->
            binding.viewAudioControl.updateSpeedText(speed)
        }
    }

    private fun setupNavigation() {
        binding.btnGoVideo.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, VideoFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewAudioControl.release()
        _binding = null
    }
}
