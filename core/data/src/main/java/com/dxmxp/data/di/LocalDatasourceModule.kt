package com.dxmxp.data.di

import com.dxmxp.data.data_source.BeerLocalDataSource
import com.dxmxp.data.data_source.BeerLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
fun interface LocalDatasourceModule {
    // Define your local data source bindings here.

    @Binds
    fun bindBeerLocalDataSource(impl: BeerLocalDataSourceImpl): BeerLocalDataSource
}
