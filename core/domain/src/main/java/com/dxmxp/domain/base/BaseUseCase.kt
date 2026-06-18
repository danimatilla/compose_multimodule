package com.dxmxp.domain.base

import kotlinx.coroutines.flow.Flow

/**
 * Base class for all synchronous/one-shot Use Cases in the domain layer.
 *
 * @param P The input parameter type. Use [Unit] if no input is required.
 * @param R The return type.
 */
abstract class BaseUseCase<in P, out R> {

    /**
     * Executes the use case.
     *
     * @param input The input parameters.
     * @return The result of the execution.
     */
    suspend operator fun invoke(input: P): R {
        return launch(input)
    }

    /**
     * Business logic implementation for the use case.
     */
    protected abstract suspend fun launch(params: P): R
}

/**
 * Specialized base class for Use Cases that return a [Flow].
 */
abstract class BaseFlowUseCase<in P, out R> {

    /**
     * Executes the use case and returns a Flow.
     */
    operator fun invoke(input: P): Flow<R> {
        return launch(input)
    }

    protected abstract fun launch(input: P): Flow<R>
}
