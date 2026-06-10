package com.dxmxp.domain.use_case

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Base class for all synchronous/one-shot Use Cases in the domain layer.
 *
 * @param P The input parameter type. Use [Unit] if no input is required.
 * @param R The return type.
 */
abstract class BaseUseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {

    /**
     * Executes the use case.
     *
     * @param params The input parameters.
     * @return The result of the execution.
     */
    suspend operator fun invoke(params: P): R {
        return withContext(dispatcher) {
            launch(params)
        }
    }

    /**
     * Business logic implementation for the use case.
     */
    protected abstract suspend fun launch(params: P): R
}

/**
 * Specialized base class for Use Cases that return a [Flow].
 */
abstract class BaseFlowUseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {

    /**
     * Executes the use case and returns a Flow.
     */
    operator fun invoke(params: P): Flow<R> {
        return launch(params).flowOn(dispatcher)
    }

    protected abstract fun launch(params: P): Flow<R>
}
