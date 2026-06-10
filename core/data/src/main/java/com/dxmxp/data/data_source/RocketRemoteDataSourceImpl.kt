package com.dxmxp.data.data_source

import com.dxmxp.data.common.NetworkHandler
import com.dxmxp.data.common.Paginator
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import com.dxmxp.data.di.PaginatorModule.PaginatorByPage
import com.dxmxp.data.remote.api.SpaceXApi
import com.dxmxp.data.remote.dto.rocket.RocketResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RocketRemoteDataSourceImpl @Inject constructor(
    private val api: SpaceXApi.Rockets,
    @param:PaginatorByPage private val paginator: Paginator<Int, List<RocketResponse>>,
    private val networkHandler: NetworkHandler,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : RocketRemoteDataSource {

    override suspend fun fetchRockets(): List<RocketResponse>? {
        return withContext(dispatcher) {
            networkHandler.safeCall {
                paginator.fetchOneShot { api.fetchRockets() }
            }
        }
    }
}

interface RocketRemoteDataSource {
    suspend fun fetchRockets(): List<RocketResponse>?
}
