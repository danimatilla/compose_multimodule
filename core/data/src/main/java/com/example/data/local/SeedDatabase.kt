package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.ship.ShipDao
import com.example.data.local.entity.ship.ShipEntity

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        // Add your entities here.
        ShipEntity::class
    ],
)
abstract class SeedDatabase: RoomDatabase() {
    // Define DAOs here.
    abstract fun shipDao(): ShipDao
}