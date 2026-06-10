package com.dxmxp.data.di

import com.dxmxp.data.data_source.RocketLocalDataSource
import com.dxmxp.data.data_source.RocketLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocalDatasourceModule {
    // Define your local data source bindings here.

    @Binds
    fun bindRocketLocalDataSource(impl: RocketLocalDataSourceImpl): RocketLocalDataSource
}
