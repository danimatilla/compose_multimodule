package com.dxmxp.ui.common

import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.DataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * Extension to launch a [Flow] from a [CoroutineScope] and update a state through a [setState] callback.
 *
 * @param flow The [Flow] to collect.
 * @param launchIf An optional condition to check before launching the flow.
 * @param setState A callback to update the state.
 * @param onLoading An optional callback to handle loading state.
 * @param onError An optional callback to handle error state.
 * @param onSuccess A callback to handle success state.
 */
fun <T, S> CoroutineScope.launchFlow(
    flow: Flow<T>,
    setState: (S.() -> S) -> Unit,
    launchIf: Boolean = true,
    onLoading: (S.(Boolean) -> S)? = null,
    onError: (S.(String?) -> S)? = null,
    onSuccess: S.(T) -> S
) {
    if (!launchIf) return

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

/**
 * Extension to launch a [Flow] of [DataResult] from a [CoroutineScope] and update a state.
 *
 * @param flow The [Flow] of [DataResult] to collect.
 * @param launchIf An optional condition to check before launching the flow.
 * @param setState A callback to update the state.
 * @param onLoading An optional callback to handle loading state.
 * @param onError An optional callback to handle error state.
 * @param onSuccess A callback to handle success state.
 */
fun <T, S> CoroutineScope.launchResultFlow(
    flow: Flow<DataResult<T>>,
    setState: (S.() -> S) -> Unit,
    launchIf: Boolean = true,
    onLoading: (S.(isLoading: Boolean) -> S)? = null,
    onError: (S.(exception: AppException) -> S)? = null,
    onSuccess: S.(T, Boolean) -> S
) {
    if (!launchIf) return

    this.launch {
        flow.collect { result ->
            setState {
                when (result) {
                    is DataResult.Loading -> {
                        onLoading?.invoke(this, true) ?: this
                    }
                    is DataResult.Success -> {
                        val state = onLoading?.invoke(this, false) ?: this
                        state.onSuccess(result.data, result.endReached)
                    }
                    is DataResult.Error -> {
                        val state = onLoading?.invoke(this, false) ?: this
                        onError?.invoke(state, result.exception) ?: state
                    }
                }
            }
        }
    }
}
