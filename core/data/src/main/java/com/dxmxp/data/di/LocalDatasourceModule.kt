package com.dxmxp.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocalDatasourceModule {
    // Define your local data source bindings here.
}
