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
    fun provideRocketPaginator20(): Paginator<Int, RocketResponse> =
        Paginator(initialKey = 0, nextKeyProvider = { key, size -> key + size })

    @Provides
    @RocketPaginatorSize10
    fun provideRocketPaginator10(): Paginator<Int, RocketResponse> =
        Paginator(initialKey = 0, nextKeyProvider = { key, size -> key + size }, pageSize = 10)

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class RocketPaginatorDefault

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class RocketPaginatorSize10
}
