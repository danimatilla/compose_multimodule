package com.dxmxp.ui.common

import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.DataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * Extension to map the data inside a [DataResult] flow.
 * This operator is pure and supports suspend transformations.
 */
fun <T, R> Flow<DataResult<T>>.mapData(
    transform: suspend (T) -> R
): Flow<DataResult<R>> = map { result ->
    when (result) {
        is DataResult.Success -> DataResult.Success(transform(result.data), result.endReached)
        is DataResult.Error -> result
        is DataResult.Loading -> result
    }
}

/**
 * Extension to map the elements of a list inside a [DataResult] flow.
 * This operator is pure and supports suspend transformations.
 */
fun <T, R> Flow<DataResult<List<T>?>>.mapListData(
    transform: suspend (T) -> R
): Flow<DataResult<List<R>?>> = mapData { list ->
    list?.map { transform(it) }
}

/**
 * Extension to launch a [Flow] from a [CoroutineScope] and update a state through a [setState] callback.
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

/**
 * Specialized extension for paginated results.
 * It automatically manages list accumulation based on [shouldReset].
 */
fun <T, S> CoroutineScope.launchResultFlow(
    flow: Flow<DataResult<List<T>?>>,
    setState: (S.() -> S) -> Unit,
    shouldReset: Boolean,
    currentList: S.() -> List<T>?,
    launchIf: Boolean = true,
    onLoading: (S.(isLoading: Boolean) -> S)? = null,
    onError: (S.(exception: AppException) -> S)? = null,
    onSuccess: S.(List<T>, Boolean) -> S
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
                        val baseList = if (shouldReset) emptyList() else (state.currentList() ?: emptyList())
                        val newList = baseList + (result.data ?: emptyList())
                        state.onSuccess(newList, result.endReached)
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
