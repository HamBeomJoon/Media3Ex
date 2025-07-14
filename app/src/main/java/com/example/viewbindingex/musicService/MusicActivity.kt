package com.example.viewbindingex.musicService

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.viewbindingex.databinding.ActivityMusicBinding
import com.example.viewbindingex.exoplayer.VideoActivity

class MusicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMusicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initInsets(binding.clContainer)
        initInsets(binding.inViewDrawer.drawer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한 요청
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS,
                )
            } else {
                startMusicService()
            }
        } else {
            startMusicService()
        }

        binding.ivProfile.setOnClickListener {
            if (!binding.layoutDrawer.isDrawerOpen(GravityCompat.END)) {
                binding.layoutDrawer.openDrawer(GravityCompat.END)
            } else {
                binding.layoutDrawer.closeDrawer(GravityCompat.END)
            }
        }

        binding.btnGoVideo.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initInsets(targetView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(targetView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun startMusicService() {
        Handler(Looper.getMainLooper()).postDelayed({
            val serviceIntent = Intent(this, MusicService::class.java)
            startService(serviceIntent)
        }, 1_000)
    }

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001
    }
}
