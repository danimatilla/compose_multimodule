package com.dxmxp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.dxmxp.data.local.entity.BeerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BeerDao {

    @Query("SELECT * FROM beers")
    fun getBeers(): Flow<List<BeerEntity>>

    @Upsert
    suspend fun upsertBeers(rockets: List<BeerEntity>)

    @Query("DELETE FROM beers")
    suspend fun clearBeers()
}
