package com.dxmxp.data.repository

import com.dxmxp.data.data_source.IRocketRemoteDataSource
import com.dxmxp.domain.model.RocketBo
import com.dxmxp.domain.repository.IRocketRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RocketRemoteRepository(
    private val remoteDataSource: IRocketRemoteDataSource
): IRocketRemoteRepository {
    override suspend fun fetchRockets(): Flow<List<RocketBo>> {
//        return remoteDataSource.fetchRockets()
        return flow { emptyList<RocketBo>() }

    }
}