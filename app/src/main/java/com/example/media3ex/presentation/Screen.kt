package com.example.media3ex.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    object Audio : Screen("audio", "오디오", Icons.Default.Audiotrack)

    object Video : Screen("video", "비디오", Icons.Default.VideoLibrary)
}
