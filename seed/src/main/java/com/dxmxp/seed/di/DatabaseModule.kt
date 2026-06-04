<<<<<<<< HEAD:seed/src/main/java/com/dxmxp/seed/di/DatabaseModule.kt
package com.dxmxp.seed.di
========
package com.example.data.di
>>>>>>>> offline-first:core/data/src/main/java/com/example/data/di/LocalModule.kt

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
}
