package com.drbrosdev.qrscannerfromlib.util

import android.app.Activity
import androidx.window.layout.WindowMetricsCalculator

enum class WindowSizeClass {
    COMPACT, MEDIUM, EXPANDED;

    companion object {
        fun computeWindowSizeClass(activity: Activity): WindowSizeClass {
            val metrics = WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(activity)

            val widthDp = metrics.bounds.width() / activity.resources.displayMetrics.density

            return when {
                widthDp < 600f -> WindowSizeClass.COMPACT
                widthDp < 840f -> WindowSizeClass.MEDIUM
                else -> WindowSizeClass.EXPANDED
            }
        }
    }
}
