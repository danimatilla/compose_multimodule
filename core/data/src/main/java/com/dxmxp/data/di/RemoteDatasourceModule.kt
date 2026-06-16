package com.dxmxp.data.di

import com.dxmxp.data.data_source.BeerRemoteDataSource
import com.dxmxp.data.data_source.BeerRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
fun interface RemoteDatasourceModule {
    // Define your remote data source bindings here.

    @Binds
    fun bindRocketRemoteDataSource(impl: BeerRemoteDataSourceImpl): BeerRemoteDataSource
}