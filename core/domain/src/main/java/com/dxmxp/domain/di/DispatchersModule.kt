package com.dxmxp.domain.di

import javax.inject.Qualifier

/**
 * Qualifiers for Coroutine Dispatchers.
 * Defined in domain layer to be accessible by UseCases.
 */
object DispatchersModule {

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class MainDispatcher

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class IoDispatcher

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class DefaultDispatcher
}
