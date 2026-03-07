package com.example.data.remote.data_source

import com.example.data.remote.api.SpaceXApi
import com.example.data.remote.dto.ship.ShipsReqDto
import com.example.data.remote.dto.ship.res.ShipsResDto
import com.example.seed.di.CoroutineScopeModule.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShipRemoteDatasource @Inject constructor(
    private val api: SpaceXApi,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : IShipRemoteDatasource {

    override suspend fun fetchShips(shipsRequest: ShipsReqDto): ShipsResDto {
        return withContext(dispatcher) {
            api.fetchShips(shipsRequest.toRequestBody())
        }
    }
}

interface IShipRemoteDatasource {
    suspend fun fetchShips(shipsRequest: ShipsReqDto): ShipsResDto
}
