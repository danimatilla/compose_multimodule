package com.dxmxp.data.common

import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Extension function for [DataResult.Companion] to convert a suspend call into a [Flow] of [DataResult].
 * It handles emitting [DataResult.Loading], [DataResult.Success] and [DataResult.Error].
 */
fun <T> DataResult.Companion.loadingFlow(
    dispatcher: CoroutineDispatcher,
    call: suspend () -> T
): Flow<DataResult<T>> = flow {
    emit(DataResult.Loading)
    val result = call()
    emit(DataResult.Success(result))
}.catch { e ->
    val appException = e as? AppException ?: AppException.UnknownException(cause = e)
    emit(DataResult.Error(appException))
}.flowOn(dispatcher)

/**
 * Specialized extension for handling paginated flows.
 * It uses a [PaginationHandler] to manage the state of the list across emissions.
 */
fun <T> DataResult.Companion.pagingFlow(
    dispatcher: CoroutineDispatcher,
    paginationHandler: PaginationHandler<T>,
    shouldReset: Boolean,
    call: suspend () -> List<T>?
): Flow<DataResult<List<T>?>> = flow {
    emit(DataResult.Loading)
    val newItems = call()
    val result = paginationHandler.processResult(newItems, shouldReset)
    emit(DataResult.Success(data = result, endReached = paginationHandler.isEndReached))
}.catch { e ->
    val appException = e as? AppException ?: AppException.UnknownException(cause = e)
    emit(DataResult.Error(appException))
}.flowOn(dispatcher)
