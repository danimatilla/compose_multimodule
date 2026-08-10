package com.dxmxp.data.di

import com.dxmxp.data.data_source.AuthRemoteDataSource
import com.dxmxp.data.data_source.AuthRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RemoteDatasourceModule {

    @Binds
    @Singleton
    fun bindAuthRemoteDataSource(impl: AuthRemoteDataSourceImpl): AuthRemoteDataSource
}
