package com.dxmxp.data.di

import com.dxmxp.data.common.Paginator
import com.dxmxp.data.remote.dto.rocket.RocketResponse
import com.dxmxp.data.remote.dto.story.StoryResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object PaginatorModule {

    @Provides
    @PaginatorByPage
    fun provideRocketPaginatorByPage(): Paginator<Int, List<RocketResponse>> =
        Paginator(
            initialKey = 0,
            nextKeyProvider = { key, items, size ->
                if(items.size < size) null else key + size
            }
        )

    @Provides
    @PaginatorByToken
    fun provideRocketPaginatorByToken(): Paginator<String, StoryResponse> =
        Paginator(
            initialKey = "",
            nextKeyProvider = { _, result, _ ->
                result.nextToken
            }
        )

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class PaginatorByPage

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class PaginatorByToken
}
