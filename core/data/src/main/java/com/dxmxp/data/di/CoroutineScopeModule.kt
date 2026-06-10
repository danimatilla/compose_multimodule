package com.dxmxp.data.di

import com.dxmxp.domain.di.DispatchersModule.DefaultDispatcher
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import com.dxmxp.domain.di.DispatchersModule.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class IoScope

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class MainScope

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DefaultScope

    @IoScope
    @Singleton
    @Provides
    fun provideIoCoroutineScope(
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }

    @MainScope
    @Singleton
    @Provides
    fun provideMainCoroutineScope(
        @MainDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }

    @DefaultScope
    @Singleton
    @Provides
    fun provideDefaultCoroutineScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }
}
