package com.example.media3ex.presentation.audio.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.example.media3ex.presentation.audio.AudioControlViewModel
import com.example.media3ex.presentation.theme.Media3ExTheme

@UnstableApi
@Composable
fun AudioPlayerRoute(viewModel: AudioControlViewModel = hiltViewModel()) {
    val controller = rememberMediaController()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()

    LaunchedEffect(controller) {
        controller?.let {
            viewModel.initialize(
                mediaUri = "assets:///ComposeCoroutineScope.mp3",
                title = "Audio Title",
                artist = "Artist Name",
                onReady = { },
            )
        }
    }

    AudioPlayerScreen(
        isPlaying = isPlaying,
        currentPosition = currentPosition,
        duration = duration,
        playbackSpeed = playbackSpeed,
        title = "Audio Title",
        artist = "Artist Name",
        onPlayPauseClick = viewModel::togglePlayPause,
        onRewindClick = viewModel::rewind,
        onFastForwardClick = viewModel::fastForward,
        onSeekTo = viewModel::seekTo,
        onSpeedClick = viewModel::cyclePlaybackSpeed,
    )
}

// ⭐ Stateless Composable (UI만) - Preview 가능
@Composable
fun AudioPlayerScreen(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    playbackSpeed: String,
    title: String,
    artist: String,
    onPlayPauseClick: () -> Unit,
    onRewindClick: () -> Unit,
    onFastForwardClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSpeedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF121212)),
    ) {
        // 상단 영역 (앨범 아트, 제목 등)
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 앨범 아트
                Box(
                    modifier =
                        Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2C2C2C)),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = artist,
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp,
                )
            }
        }

        // 하단 컨트롤
        AudioPlayerControls(
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            playbackSpeed = playbackSpeed,
            onPlayPauseClick = onPlayPauseClick,
            onRewindClick = onRewindClick,
            onFastForwardClick = onFastForwardClick,
            onSeekTo = onSeekTo,
            onSpeedClick = onSpeedClick,
        )
    }
}

@Preview(showBackground = true, name = "재생 중")
@Composable
private fun AudioPlayerScreenPlayingPreview() {
    Media3ExTheme {
        AudioPlayerScreen(
            isPlaying = true,
            currentPosition = 125000L,
            duration = 550000L,
            playbackSpeed = "1.5x",
            title = "Compose Coroutine Scope",
            artist = "Tech Podcast",
            onPlayPauseClick = {},
            onRewindClick = {},
            onFastForwardClick = {},
            onSeekTo = {},
            onSpeedClick = {},
        )
    }
}
