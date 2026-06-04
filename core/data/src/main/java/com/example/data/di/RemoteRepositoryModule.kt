package com.example.data.di

import com.example.data.remote.repository.RocketRemoteRepository
import com.example.domain.repository.remote.IRocketRemoteRepository
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