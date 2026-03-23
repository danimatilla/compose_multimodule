package com.example.data.di

import com.example.data.local.data_source.IShipLocalDataSource
import com.example.data.local.data_source.ShipLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocalDatasourceModule {

    @Binds
    fun bindShipLocalDatasource(impl: ShipLocalDataSource): IShipLocalDataSource
}