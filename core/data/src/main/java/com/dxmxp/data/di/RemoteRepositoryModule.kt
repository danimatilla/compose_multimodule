package com.dxmxp.data.di

import com.dxmxp.data.repository.RocketRemoteRepository
import com.dxmxp.domain.repository.IRocketRemoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteRepositoryModule {
    // Define your remote repositories bindings here.

    @Binds
    fun bindRocketRemoteRepository(impl: RocketRemoteRepository): IRocketRemoteRepository
}