package com.drbrosdev.qrscannerfromlib.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
Grab dependencies here:
    * repeatOnLifeCycle: 'androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha02'
 */

/*
Takes a StateFlow as a parameter and renders(collects) it in a safe way using repeatOnLifecycle.
Could be used with a data class representing the screen state just like in a Mavericks or MVI
paradigm. The data class could use the a sealed class to represent different states when async loading
data - from network or database.
 */
inline fun <T> Fragment.collectStateFlow(state: StateFlow<T>, crossinline render: ((T) -> Unit)) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            state.collect { state ->
                render(state)
            }
        }
    }
}

/*
Same thing as above, just takes a flow instead.
 */
inline fun <T> Fragment.collectFlow(state: Flow<T>, crossinline render: ((T) -> Unit)) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            state.collect { state ->
                render(state)
            }
        }
    }
}

/*
ViewModels can create Channels and use sealed classes for events. Events are used as one-time
notifications from the viewModel to the fragment. This piece of code is just to reduce clutter
with coroutines and provide a concise api.
 */
inline fun <T> Fragment.collectEvents(events: Flow<T>, crossinline onCollected: ((T) -> Unit)) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            events.collect { onCollected(it) }
        }
    }
}
//allows a code block to perform any other needed operations to the data before its set
//with copy, syntax sugar essentially -- stateFlow.setState { ... copy(...) }
inline fun <T> MutableStateFlow<T>.setState(action: T.() -> T) {
    this.value = this.value.action()
}