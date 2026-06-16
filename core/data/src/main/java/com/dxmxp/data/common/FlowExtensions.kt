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
