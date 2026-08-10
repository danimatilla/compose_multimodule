package com.dxmxp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dxmxp.data.local.dao.UserDao
import com.dxmxp.data.local.entity.UserEntity

@Database(
    version = 2,
    exportSchema = false,
    entities = [
        // Add your entities here.
        UserEntity::class
    ],
)
abstract class SeedDatabase : RoomDatabase() {
    // Define DAOs here.
    abstract val userDao: UserDao
}
