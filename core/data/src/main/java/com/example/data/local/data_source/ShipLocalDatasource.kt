package com.example.data.local.data_source

import com.example.data.local.dao.ship.ShipDao
import com.example.data.local.entity.ship.ShipEntity
import com.example.seed.di.CoroutineScopeModule.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class ShipLocalDatasource @Inject constructor(
    private val dao: ShipDao,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : IShipLocalDatasource {

    override fun getShips(): Flow<List<ShipEntity>> {
        return dao.getShips().flowOn(dispatcher)
    }

    override fun getShip(id: String): Flow<ShipEntity> {
        return dao.getShip(id).flowOn(dispatcher)
    }

    override suspend fun saveShip(ship: ShipEntity) {
        dao.saveShip(ship)
    }
}

interface IShipLocalDatasource {
    fun getShips(): Flow<List<ShipEntity>>
    fun getShip(id: String): Flow<ShipEntity>
    suspend fun saveShip(ship: ShipEntity)
}
