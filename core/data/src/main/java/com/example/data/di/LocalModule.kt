package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.SeedDatabase
import com.example.data.local.dao.ship.ShipDao
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
    fun provideShipDao(database: SeedDatabase): ShipDao {
        return database.shipDao()
    }

    @Provides
    @Singleton
    fun provideSeedDatabase(
        @ApplicationContext context: Context
    ): SeedDatabase = Room.databaseBuilder(
        context = context,
        klass = SeedDatabase::class.java,
        name = "seed_database"
    ).build()
}
