package com.dxmxp.data.di

import com.dxmxp.data.common.Paginator
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
    @PaginatorByToken
    fun providePaginatorByToken(): Paginator<String, StoryResponse> =
        Paginator(
            initialKey = "",
            nextKeyProvider = { _, result, _ ->
                result.nextToken
            }
        )

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class PaginatorByToken
}
