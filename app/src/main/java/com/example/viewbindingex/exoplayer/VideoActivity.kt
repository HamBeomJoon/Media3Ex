package com.example.viewbindingex.exoplayer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.viewbindingex.R
import com.example.viewbindingex.databinding.ActivityVideoBinding
import com.example.viewbindingex.musicService.MusicActivity

class VideoActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private val viewModel: VideoViewModel by viewModels()
    private lateinit var binding: ActivityVideoBinding

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        // ExoPlayer 생성 및 설정
        player =
            ExoPlayer
                .Builder(this)
                .setSeekBackIncrementMs(5000) // 뒤로가기 5초
                .setSeekForwardIncrementMs(5000) // 앞으로가기 5초
                .build()
                .apply {
                    val mediaItem =
                        MediaItem.fromUri("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3".toUri())
                    setMediaItem(mediaItem)
                    prepare()
                    playWhenReady = true
                }

        // PlayerControlView 연결
        binding.playerView.player = player

        // 컨트롤러 내부 버튼 직접 참조
        val playButton = binding.playerView.findViewById<ImageView>(R.id.exo_play)
        playButton?.setOnClickListener {
            viewModel.togglePlayPause()
        }

        // ViewModel 관찰하여 UI/플레이어 상태 제어
        viewModel.isPlaying.observe(this) { isPlaying ->
            if (isPlaying) player?.play() else player?.pause()

            // 버튼 이미지 변경 (bindingAdapter 또는 직접)
            playButton?.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
            )
        }

        binding.playerView.findViewById<ImageView>(R.id.exo_rew)?.setOnClickListener {
            player?.seekBack() // 기본적으로 5초
        }

        // forward 버튼
        binding.playerView.findViewById<ImageView>(R.id.exo_ffwd)?.setOnClickListener {
            player?.seekForward() // 기본적으로 5초
        }

        binding.tvBack.setOnClickListener {
            val intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
