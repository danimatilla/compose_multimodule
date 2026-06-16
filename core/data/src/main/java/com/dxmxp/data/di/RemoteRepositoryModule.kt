package com.dxmxp.data.di

import com.dxmxp.data.repository.BeerRemoteRepositoryImpl
import com.dxmxp.domain.repository.BeerRemoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
fun interface RemoteRepositoryModule {
    // Define your remote repositories bindings here.

    @Binds
    fun bindRocketRemoteRepository(impl: BeerRemoteRepositoryImpl): BeerRemoteRepository
}