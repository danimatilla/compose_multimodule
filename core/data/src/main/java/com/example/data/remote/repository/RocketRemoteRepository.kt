package com.example.data.remote.repository

import com.example.data.remote.data_source.IRocketRemoteDataSource
import com.example.domain.bo.rocket.RocketBo
import com.example.domain.repository.remote.IRocketRemoteRepository
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