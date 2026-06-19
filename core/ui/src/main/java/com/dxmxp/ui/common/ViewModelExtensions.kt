package com.dxmxp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * Creates a debounced action within the scope of a [ViewModel].
 *
 * @param delayMillis The debounce delay in milliseconds.
 * @param action The action to be executed after the delay.
 * @return A function that triggers the debounced action.
 */
@OptIn(FlowPreview::class)
fun <T> ViewModel.debounce(
    delayMillis: Long = 500L,
    action: (T) -> Unit
): (T) -> Unit {
    val flow = MutableSharedFlow<T>(extraBufferCapacity = 1)
    viewModelScope.launch {
        flow.debounce(delayMillis).collect { action(it) }
    }
    return { flow.tryEmit(it) }
}
