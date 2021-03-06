package com.drbrosdev.qrscannerfromlib

import android.app.Application
import com.drbrosdev.qrscannerfromlib.di.appModule
import com.drbrosdev.qrscannerfromlib.di.repoModule
import com.drbrosdev.qrscannerfromlib.di.viewModelModule
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(appModule, repoModule, viewModelModule))
        }
    }
}