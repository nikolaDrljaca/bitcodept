package com.drbrosdev.qrscannerfromlib.ui.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.drbrosdev.qrscannerfromlib.databinding.ActivityOnboardingBinding
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.datastore.datastore
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOnboardingBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        updateWindowInsets(binding.root)

        val prefs = AppPreferences(this.datastore)

        binding.apply {
            buttonLetsGo.setOnClickListener {
                lifecycleScope.launch { prefs.firstLaunchComplete() }
                finish()
            }
            animationView.repeatCount = 3
        }
    }
}