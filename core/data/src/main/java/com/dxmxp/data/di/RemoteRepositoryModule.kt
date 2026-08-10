package com.dxmxp.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteRepositoryModule {
    // Define your remote repositories bindings here.
}
