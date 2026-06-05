package com.dxmxp.data.di

import com.dxmxp.data.common.Paginator
import com.dxmxp.data.remote.dto.rocket.RocketResponse
import com.dxmxp.data.remote.dto.story.StoryResponse
import com.dxmxp.domain.model.RocketBo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object PaginatorModule {

    @Provides
    @RocketPaginatorByPage
    fun provideRocketPaginatorByPage(): Paginator<Int, List<RocketResponse>> =
        Paginator(
            initialKey = 0,
            nextKeyProvider = { key, items, size ->
                if(items.size < size) null else key + size
            }
        )

    @Provides
    @RocketPaginatorByToken
    fun provideRocketPaginatorByToken(): Paginator<String, StoryResponse> =
        Paginator(
            initialKey = "",
            nextKeyProvider = { _, result, _ ->
                result.nextToken
            }
        )

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    /**
     * Default paginator for rockets, with a page size of 20 and a simple next key provider that increments the key by the page size.
     */
    annotation class RocketPaginatorByPage

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class RocketPaginatorByToken
}
