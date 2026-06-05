package com.dxmxp.data.di

import com.dxmxp.data.common.Paginator
import com.dxmxp.data.remote.dto.rocket.RocketResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object PaginatorModule {

    @Provides
    @RocketPaginatorDefault
    fun provideRocketPaginatorDefault(): Paginator<Int, RocketResponse> =
        Paginator(initialKey = 0, nextKeyProvider = { key, size -> key + size })

    @Provides
    @RocketPaginatorSize10
    fun provideRocketPaginatorSize10(): Paginator<Int, RocketResponse> =
        Paginator(initialKey = 0, nextKeyProvider = { key, size -> key + size }, pageSize = 10)

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    /**
     * Default paginator for rockets, with a page size of 20 and a simple next key provider that increments the key by the page size.
     */
    annotation class RocketPaginatorDefault

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    /**
     * Paginator for rockets with a page size of 10, and a next key provider that increments the key by the page size. This can be used when a smaller page size is desired for fetching rockets.
     */
    annotation class RocketPaginatorSize10
}
