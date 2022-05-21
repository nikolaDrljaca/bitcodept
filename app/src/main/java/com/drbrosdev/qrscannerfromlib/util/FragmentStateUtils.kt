package com.drbrosdev.qrscannerfromlib.util

import androidx.fragment.app.DialogFragment
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
inline fun <T> Fragment.collectFlow(
    flow: Flow<T>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: ((T) -> Unit)
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            flow.collect { action(it) }
        }
    }
}

/*
When working with [DialogFragment] and its subclasses it is recommended to use
the fragment itself for coroutine related stuff rather than the [viewLifecycleOwner].
 */
inline fun <T> DialogFragment.collectFlow(
    flow: Flow<T>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: ((T) -> Unit)
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(lifecycleState) {
            flow.collect { action(it) }
        }
    }
}