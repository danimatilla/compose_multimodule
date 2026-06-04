package com.dxmxp.data.data_source

import com.dxmxp.data.di.CoroutineScopeModule
import com.dxmxp.data.remote.api.SpaceXApi
import com.dxmxp.data.remote.dto.rocket.RocketResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RocketRemoteDataSource @Inject constructor(
    private val api: SpaceXApi.Rockets,
    @param:CoroutineScopeModule.IoDispatcher private val dispatcher: CoroutineDispatcher
) : IRocketRemoteDataSource {

    override suspend fun fetchRockets(): List<RocketResponse>? {
        return withContext(dispatcher) {
            api.fetchRockets()
        }
    }
}

interface IRocketRemoteDataSource {
    suspend fun fetchRockets(): List<RocketResponse>?
}
