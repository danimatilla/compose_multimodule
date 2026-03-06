package com.example.data.local

import androidx.room.RoomDatabase

//@Database(
//    version = 1,
//    exportSchema = true,
//    entities = [
//        /* Your entities here */
//    ],
//)
abstract class SeedDatabase: RoomDatabase() {
    // Define DAOs here, for example:
    // fun userDao(): UserDao
}