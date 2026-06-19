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

/**
 * Extension to launch a [Flow] of [DataResult] from a [CoroutineScope] and update a state.
 */
fun <T, S> CoroutineScope.launchResultFlow(
    flow: Flow<DataResult<T>>,
    setState: (S.() -> S) -> Unit,
    onLoading: (S.(isLoading: Boolean) -> S)? = null,
    onError: (S.(exception: AppException) -> S)? = null,
    onSuccess: S.(T) -> S
) {
    this.launch {
        flow.collect { result ->
            setState {
                when (result) {
                    is DataResult.Loading -> {
                        onLoading?.invoke(this, true) ?: this
                    }
                    is DataResult.Success -> {
                        val state = onLoading?.invoke(this, false) ?: this
                        state.onSuccess(result.data)
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
