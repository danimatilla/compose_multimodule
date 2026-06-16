package com.dxmxp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dxmxp.data.local.dao.BeerDao
import com.dxmxp.data.local.entity.BeerEntity

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        // Add your entities here.
        BeerEntity::class
    ],
)
abstract class SeedDatabase : RoomDatabase() {
    // Define DAOs here.
    abstract val beerDao: BeerDao
}
