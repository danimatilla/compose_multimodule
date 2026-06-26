package com.dxmxp.data.di

import android.content.Context
import androidx.room.Room
import com.dxmxp.data.local.SeedDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideSeedDatabase(
        @ApplicationContext context: Context
    ): SeedDatabase = Room.databaseBuilder(
        context = context,
        klass = SeedDatabase::class.java,
        name = "seed_database"
    ).fallbackToDestructiveMigration(false).build()

    @Provides
    @Singleton
    fun provideBeerDao(database: SeedDatabase) = database.beerDao

    @Provides
    @Singleton
    fun provideUserDao(database: SeedDatabase) = database.userDao

    @Provides
    @Singleton
    fun provideCryptoManager(@ApplicationContext context: Context): com.dxmxp.data.local.datastore.CryptoManager =
        com.dxmxp.data.local.datastore.CryptoManager(context)

    @Provides
    @Singleton
    fun provideSessionDataStore(
        @ApplicationContext context: Context,
        cryptoManager: com.dxmxp.data.local.datastore.CryptoManager
    ): com.dxmxp.data.local.datastore.SessionDataStore =
        com.dxmxp.data.local.datastore.SessionDataStore(context, cryptoManager)
}
