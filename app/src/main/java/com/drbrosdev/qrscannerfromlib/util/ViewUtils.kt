package com.drbrosdev.qrscannerfromlib.util

import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun RecyclerView.heightAsFlow() = callbackFlow {
    doOnLayout {
        trySend(it.measuredHeight)
    }

    awaitClose {  }
}