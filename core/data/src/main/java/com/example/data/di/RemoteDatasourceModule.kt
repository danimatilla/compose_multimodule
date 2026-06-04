package com.example.data.di

import com.example.data.remote.data_source.IRocketRemoteDataSource
import com.example.data.remote.data_source.RocketRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteDatasourceModule {
    // Define your remote data source bindings here.

    @Binds
    fun bindRocketRemoteDataSource(impl: RocketRemoteDataSource): IRocketRemoteDataSource
}