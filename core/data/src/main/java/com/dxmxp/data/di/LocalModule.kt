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
    ).build()

    // Provides DAOs here, for example:
    // @Provides
    // @Singleton
    // fun provideUserDao(database: AppDatabase): UserDao {
    //     return database.userDao()
    // }
}
