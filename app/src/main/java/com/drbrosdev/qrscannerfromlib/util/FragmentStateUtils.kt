package com.drbrosdev.qrscannerfromlib.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
Grab dependencies here:
    * repeatOnLifeCycle: 'androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha02'
 */
inline fun <T> Fragment.collectFlow(flow: Flow<T>, crossinline action: ((T) -> Unit)) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { action(it) }
        }
    }
}