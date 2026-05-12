package com.dxmxp.seed.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    // Provides DAOs here, for example:
    // @Provides
    // @Singleton
    // fun provideUserDao(database: AppDatabase): UserDao {
    //     return database.userDao()
    // }
}