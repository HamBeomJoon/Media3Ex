package com.example.media3ex.presentation.audio.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media3ex.presentation.theme.Media3ExTheme

@Composable
fun AudioPlayerControls(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    playbackSpeed: String,
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
                .fillMaxWidth()
                .background(Color(0xFF1C1C1C)) // hearit_black1
                .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 시간 표시와 프로그레스바
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 현재 위치
            Text(
                text = formatTime(currentPosition),
                color = Color(0xFFB0B0B0), // hearit_gray4
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp),
            )

            // 전체 시간
            Text(
                text = formatTime(duration),
                color = Color(0xFFB0B0B0), // hearit_gray4
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 4.dp),
            )
        }

        // 프로그레스바
        Slider(
            value = if (duration > 0) currentPosition.toFloat() else 0f,
            onValueChange = { onSeekTo(it.toLong()) },
            valueRange = 0f..duration.toFloat(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            colors =
                SliderDefaults.colors(
                    thumbColor = Color(0xFF9C6FDE), // hearit_purple3
                    activeTrackColor = Color(0xFF9C6FDE), // hearit_purple3
                    inactiveTrackColor = Color(0xFF3D3D3D), // hearit_gray1
                ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 컨트롤 버튼들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 되감기 버튼
            IconButton(
                onClick = onRewindClick,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "10초 뒤로",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }

            // 재생/일시정지 버튼
            IconButton(
                onClick = onPlayPauseClick,
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9C6FDE)), // hearit_purple3
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "일시정지" else "재생",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }

            // 빨리감기 버튼
            IconButton(
                onClick = onFastForwardClick,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "10초 앞으로",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }

            // 재생 속도 버튼
            Text(
                text = playbackSpeed,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier =
                    Modifier
                        .clickable(onClick = onSpeedClick)
                        .padding(8.dp),
            )
        }
    }
}

// 시간 포맷 함수 (밀리초 → MM:SS)
private fun formatTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview(showBackground = true)
@Composable
private fun AudioPlayerControlsPreview() {
    Media3ExTheme {
        AudioPlayerControls(
            isPlaying = false,
            currentPosition = 0L,
            duration = 550000L,
            playbackSpeed = "1.0x",
            onPlayPauseClick = {},
            onRewindClick = {},
            onFastForwardClick = {},
            onSeekTo = {},
            onSpeedClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AudioPlayerControlsPlayingPreview() {
    Media3ExTheme {
        AudioPlayerControls(
            isPlaying = true,
            currentPosition = 125000L, // 2:05
            duration = 550000L, // 9:10
            playbackSpeed = "1.5x",
            onPlayPauseClick = {},
            onRewindClick = {},
            onFastForwardClick = {},
            onSeekTo = {},
            onSpeedClick = {},
        )
    }
}
