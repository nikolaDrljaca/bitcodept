package com.drbrosdev.qrscannerfromlib

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.datastore.datastore
import com.drbrosdev.qrscannerfromlib.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //logger
        //draw behind system bars, for edge to edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        val prefs = AppPreferences(this.datastore)
        lifecycleScope.launch {
            prefs.isFirstLaunch.collect {
                if (!it) {
                    val foo = Intent(this@MainActivity, OnboardingActivity::class.java)
                    startActivity(foo)
                }
            }
        }
    }
}