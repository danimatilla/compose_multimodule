package com.example.data.remote.data_source

import com.example.data.di.CoroutineScopeModule
import com.example.data.di.RemoteModule
import com.example.data.remote.api.SpaceXApi
import com.example.data.remote.dto.ship.ShipsReqDto
import com.example.data.remote.dto.ship.ShipsResDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShipRemoteDataSource @Inject constructor(
    @param:RemoteModule.ShipApi private val api: SpaceXApi.Ships,
    @param:CoroutineScopeModule.IoDispatcher private val dispatcher: CoroutineDispatcher
) : IShipRemoteDataSource {

    override suspend fun fetchShips(shipsRequest: ShipsReqDto): ShipsResDto {
        return withContext(dispatcher) {
            api.fetchShips(shipsRequest.toRequestBody())
        }
    }
}

interface IShipRemoteDataSource {
    suspend fun fetchShips(shipsRequest: ShipsReqDto): ShipsResDto
}
