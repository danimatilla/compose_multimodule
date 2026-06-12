package com.dxmxp.ui.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * Extension to launch a [Flow] from a [CoroutineScope] and update a state through a [setState] callback.
 * This is an idiomatic way to handle data flows in ViewModels when the base class cannot be modified.
 */
fun <T, S> CoroutineScope.launchFlow(
    flow: Flow<T>,
    setState: (S.() -> S) -> Unit,
    onLoading: (S.(Boolean) -> S)? = null,
    onError: (S.(String?) -> S)? = null,
    onSuccess: S.(T) -> S
) {
    this.launch {
        flow.onStart {
            setState {
                var state = this
                onLoading?.let { state = state.it(true) }
                onError?.let { state = state.it(null) }
                state
            }
        }.catch { e ->
            val msg = e.localizedMessage ?: "Unknown error"
            setState {
                var state = this
                onLoading?.let { state = state.it(false) }
                onError?.let { state = state.it(msg) }
                state
            }
        }.collect { data ->
            setState {
                var state = this
                onLoading?.let { state = state.it(false) }
                state.onSuccess(data)
            }
        }
    }
}
