package com.example.viewbindingex.card

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(
        view: View,
        position: Float,
    ) {
        val scale = 0.85f.coerceAtLeast(1 - abs(position * 0.15f))
        val alpha = 0.5f.coerceAtLeast(1 - abs(position * 0.5f))

        view.scaleX = scale
        view.scaleY = scale
        view.alpha = alpha
        view.translationZ = if (position == 0f) 1f else 0f // 가운데 카드 위로
    }
}
