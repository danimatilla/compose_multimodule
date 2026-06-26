package com.dxmxp.domain.base

import kotlinx.coroutines.flow.Flow

/**
 * Base class for all synchronous/one-shot Use Cases in the domain layer.
 *
 * @param I The input parameter type. Use [Unit] if no input is required.
 * @param O The return type.
 */
abstract class BaseUseCase<in I, out O> {

    /**
     * Executes the use case.
     *
     * @param input The input parameters.
     * @return The result of the execution.
     */
    suspend operator fun invoke(input: I): O {
        return launch(input)
    }

    /**
     * Business logic implementation for the use case.
     */
    protected abstract suspend fun launch(input: I): O
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
