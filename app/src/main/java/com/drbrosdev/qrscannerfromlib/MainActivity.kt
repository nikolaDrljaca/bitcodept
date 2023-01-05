package com.drbrosdev.qrscannerfromlib

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.datastore.datastore
import com.drbrosdev.qrscannerfromlib.ui.onboarding.OnboardingActivity
import com.google.android.play.core.review.ReviewManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {
    private val reviewManager: ReviewManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //logger
        //draw behind system bars, for edge to edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        val prefs = AppPreferences(this.datastore)
        lifecycleScope.launch {
            prefs.incrementReviewDialog()
            val firstLaunch = prefs.isFirstLaunch.first()
            if (!firstLaunch) {
                val foo = Intent(this@MainActivity, OnboardingActivity::class.java)
                startActivity(foo)
            }

            val showReviewDialog = prefs.showRateDialog.first()
            if (showReviewDialog == 9) {
                val request = reviewManager.requestReviewFlow()
                request.addOnCompleteListener {
                    if (request.isSuccessful) {
                        val reviewInfo = it.result
                        val flow = reviewManager.launchReviewFlow(this@MainActivity, reviewInfo)
                    }
                }
            }
        }

        if (intent.action == Intent.ACTION_SEND) {
            if (intent.type?.startsWith("image") == true) {
                findNavController(R.id.nav_host_frag).navigateUp()
                findNavController(R.id.nav_host_frag)
                    .navigate(R.id.localImageFragment)
            }
        }
    }
}