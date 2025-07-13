package com.example.viewbindingex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.viewbindingex.card.CardAdapter
import com.example.viewbindingex.card.CardItem
import com.example.viewbindingex.card.ZoomOutPageTransformer
import com.example.viewbindingex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카드 데이터 예시
        val cards =
            listOf(
                CardItem("1", R.drawable.ic_launcher_foreground),
                CardItem("2", R.drawable.ic_launcher_foreground),
                CardItem("3", R.drawable.ic_launcher_foreground),
                CardItem("4", R.drawable.ic_launcher_foreground),
                CardItem("5", R.drawable.ic_launcher_foreground),
                CardItem("6", R.drawable.ic_launcher_foreground),
                CardItem("7", R.drawable.ic_launcher_foreground),
                CardItem("8", R.drawable.ic_launcher_foreground),
                CardItem("9", R.drawable.ic_launcher_foreground),
            )

        adapter = CardAdapter(cards)
        binding.viewPager.adapter = adapter

        // 가운데 카드만 크게 보이게 하는 애니메이션 효과
        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())

        // 페이지 간 거리 조정 (카드 겹침 느낌)
        binding.viewPager.offscreenPageLimit = 5
    }
}
