package com.example.data.local.dao.ship

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.data.local.entity.ShipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipDao {

    @Query(GET_SHIPS_QUERY)
    fun getShips(): Flow<List<ShipEntity>>

    @Query(GET_SHIP_BY_ID_QUERY)
    fun getShip(id: String): Flow<ShipEntity>

    @Upsert
    suspend fun saveShip(ship: ShipEntity)

    companion object{
        const val GET_SHIPS_QUERY = "SELECT * FROM ships"
        const val GET_SHIP_BY_ID_QUERY = "SELECT * FROM ships WHERE id = :id"
    }
}