package com.bibitdev.storyapps.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.bibitdev.storyapps.databinding.ActivitySplashScreenBinding
import com.bibitdev.storyapps.ui.authentication.login.LoginActivity
import com.bibitdev.storyapps.ui.home.HomeActivity
import com.bibitdev.storyapps.utils.PreferencesHelper

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = PreferencesHelper(this)

        playAnimations()

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000)
    }

    private fun playAnimations() {
        binding.ivTopShape.animate()
            .translationX(-500f)
            .alpha(0f)
            .setDuration(1500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.ivBottomShape.animate()
            .translationX(500f)
            .alpha(0f)
            .setDuration(1500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.ivSplashScreen.alpha = 0f
        binding.ivSplashScreen.animate()
            .alpha(1f)
            .setDuration(1500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.tvTitleSplash.alpha = 0f
        binding.tvTitleSplash.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.tvSubSplash.alpha = 0f
        binding.tvSubSplash.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(1000)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun navigateToNextScreen() {
        val user = preferencesHelper.loadUser()
        if (user != null) {
            Intent(this, HomeActivity::class.java).also {
                startActivity(it)
                finish()
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
