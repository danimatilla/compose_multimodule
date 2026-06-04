package com.dxmxp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dxmxp.data.local.entity.RocketEntity

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        // Add your entities here.
        RocketEntity::class
    ],
)
abstract class SeedDatabase : RoomDatabase() {
    // Define DAOs here.
}