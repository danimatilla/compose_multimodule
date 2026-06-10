package com.dxmxp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.dxmxp.data.local.entity.RocketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RocketDao {

    @Query("SELECT * FROM rockets")
    fun getRockets(): Flow<List<RocketEntity>>

    @Upsert
    suspend fun upsertRockets(rockets: List<RocketEntity>)

    @Query("DELETE FROM rockets")
    suspend fun clearRockets()
}
