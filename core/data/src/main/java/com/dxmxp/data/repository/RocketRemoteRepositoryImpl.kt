package com.dxmxp.data.repository

import com.dxmxp.data.data_source.RocketRemoteDataSource
import com.dxmxp.domain.model.Rocket
import com.dxmxp.domain.repository.RocketRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RocketRemoteRepositoryImpl(
    private val remoteDataSource: RocketRemoteDataSource
): RocketRemoteRepository {
    override fun getRockets(): Flow<List<Rocket>> {
//        return remoteDataSource.fetchRockets()
        return flow { emptyList<Rocket>() }

    }
}