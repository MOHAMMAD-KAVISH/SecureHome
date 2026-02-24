package com.example.securehomeplus.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.securehomeplus.databinding.ActivitySplashBinding
import com.example.securehomeplus.ui.login.LoginActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashDuration = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start animation effects
        startAnimations()

        // Navigate to Login screen after delay
        CoroutineScope(Dispatchers.Main).launch {
            delay(splashDuration)
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun startAnimations() {
        // Scale-up Lottie animation
        binding.lottieAnim.scaleX = 0.7f
        binding.lottieAnim.scaleY = 0.7f
        binding.lottieAnim.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1200)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Fade-in app name
        binding.appName.alpha = 0f
        binding.appName.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
        binding.tvQuote.alpha = 0f
        binding.tvQuote.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
}
