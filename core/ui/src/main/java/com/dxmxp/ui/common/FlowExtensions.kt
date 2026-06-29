package com.dxmxp.ui.common

import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.DataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Extension to map the data inside a [DataResult] flow.
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
 */
fun <T, R> Flow<DataResult<List<T>?>>.mapListData(
    transform: suspend (T) -> R
): Flow<DataResult<List<R>?>> = mapData { list ->
    list?.map { transform(it) }
}

/**
 * Clean alternative to handle Flow<DataResult> collection and update state.
 * Reducer returns S? to allow skipping state updates (avoiding empty copy()).
 */
fun <T, S> CoroutineScope.collectInto(
    flow: Flow<DataResult<T>>,
    setState: (S.() -> S) -> Unit,
    launchIf: Boolean = true,
    reducer: S.(DataResult<T>) -> S?
): Job? {
    if (!launchIf) return null
    return launch {
        flow.collect { result ->
            setState { reducer(result) ?: this }
        }
    }
}

/**
 * Specialized version for paging results.
 * Reducer returns S? to allow skipping state updates.
 */
fun <T, S> CoroutineScope.collectPagingInto(
    flow: Flow<DataResult<List<T>?>>,
    setState: (S.() -> S) -> Unit,
    shouldReset: Boolean,
    currentList: S.() -> List<T>?,
    launchIf: Boolean = true,
    reducer: S.(combinedList: List<T>, endReached: Boolean, exception: AppException?) -> S?
): Job? {
    if (!launchIf) return null
    return launch {
        flow.collect { result ->
            setState {
                when (result) {
                    is DataResult.Loading -> reducer(emptyList(), false, null) ?: this
                    is DataResult.Success -> {
                        val baseList = if (shouldReset) emptyList() else (this.currentList() ?: emptyList())
                        val newList = baseList + (result.data ?: emptyList())
                        reducer(newList, result.endReached, null) ?: this
                    }
                    is DataResult.Error -> reducer(emptyList(), false, result.exception) ?: this
                }
            }
        }
    }
}
