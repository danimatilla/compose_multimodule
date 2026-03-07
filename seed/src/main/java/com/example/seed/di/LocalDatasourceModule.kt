package com.example.seed.di

import com.example.data.local.data_source.IShipLocalDatasource
import com.example.data.local.data_source.ShipLocalDatasource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocalDatasourceModule {

    @Binds
    fun bindShipLocalDatasource(impl: ShipLocalDatasource): IShipLocalDatasource
}