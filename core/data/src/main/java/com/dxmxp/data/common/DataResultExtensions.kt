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
 * @param dispatcher The dispatcher to run the call on.
 * @param pageSize The number of items per page to determine if the end is reached.
 * @param call The suspend function to fetch the list of items.
 */
fun <T> DataResult.Companion.pagingFlow(
    dispatcher: CoroutineDispatcher,
    pageSize: Int = 20,
    call: suspend () -> List<T>?
): Flow<DataResult<List<T>?>> = flow {
    emit(DataResult.Loading)
    val result = call()
    val endReached = (result?.size ?: 0) < pageSize
    emit(DataResult.Success(data = result, endReached = endReached))
}.catch { e ->
    val appException = e as? AppException ?: AppException.UnknownException(cause = e)
    emit(DataResult.Error(appException))
}.flowOn(dispatcher)
