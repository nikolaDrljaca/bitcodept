package com.drbrosdev.qrscannerfromlib.util

import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun RecyclerView.heightAsFlow() = callbackFlow {
    kotlinx.coroutines.delay(100)
    doOnLayout {
        trySend(it.measuredHeight)
    }

    awaitClose {  }
}

fun TextView.setDrawable(@DrawableRes res: Int, @DimenRes sizeRes: Int) {
    val drawable = ContextCompat.getDrawable(context, res)
    val size = resources.getDimensionPixelSize(sizeRes)
    drawable?.setBounds(0,0, size, size)
    setCompoundDrawablesRelative(null, null, drawable, null)
}