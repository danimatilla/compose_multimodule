package com.dxmxp.data.di

import com.dxmxp.data.common.logging.SeedLogger
import com.dxmxp.domain.base.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LoggerModule {

    @Binds
    @Singleton
    fun bindLogger(impl: SeedLogger): Logger
}
