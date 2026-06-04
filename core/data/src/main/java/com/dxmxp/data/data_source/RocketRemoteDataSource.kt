package com.dxmxp.data.data_source

import com.dxmxp.data.common.Paginator
import com.dxmxp.data.di.CoroutineScopeModule.IoDispatcher
import com.dxmxp.data.di.PaginatorModule.RocketPaginatorDefault
import com.dxmxp.data.remote.api.SpaceXApi
import com.dxmxp.data.remote.dto.rocket.RocketResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RocketRemoteDataSource @Inject constructor(
    private val api: SpaceXApi.Rockets,
    @param:RocketPaginatorDefault private val paginator: Paginator<Int, RocketResponse>,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : IRocketRemoteDataSource {

    override suspend fun fetchRockets(): List<RocketResponse>? {
        return withContext(dispatcher) {
            paginator.fetchOneShot(api::fetchRockets)
        }
    }
}

interface IRocketRemoteDataSource {
    suspend fun fetchRockets(): List<RocketResponse>?
}
