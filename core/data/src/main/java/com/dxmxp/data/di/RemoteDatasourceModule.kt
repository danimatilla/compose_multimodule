package com.dxmxp.data.di

import com.dxmxp.data.data_source.RocketRemoteDataSource
import com.dxmxp.data.data_source.RocketRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteDatasourceModule {
    // Define your remote data source bindings here.

    @Binds
    fun bindRocketRemoteDataSource(impl: RocketRemoteDataSourceImpl): RocketRemoteDataSource
}