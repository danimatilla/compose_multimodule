package com.dxmxp.data.repository

import com.dxmxp.data.data_source.RocketLocalDataSource
import com.dxmxp.data.data_source.RocketRemoteDataSource
import com.dxmxp.data.repository.mapper.RocketMapper.toDomain
import com.dxmxp.data.repository.mapper.RocketMapper.toEntity
import com.dxmxp.domain.model.Rocket
import com.dxmxp.domain.repository.RocketRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * Implementation of [RocketRemoteRepository] using an Offline-first strategy.
 * It uses [RocketLocalDataSource] as the Single Source of Truth (SSOT).
 */
class RocketRemoteRepositoryImpl @Inject constructor(
    private val remoteDataSource: RocketRemoteDataSource,
    private val localDataSource: RocketLocalDataSource,
) : RocketRemoteRepository {

    override fun getRockets(): Flow<List<Rocket>> = flow {
        // Emit data from local database first, then try to refresh from remote.
        emitAll(
            localDataSource.getRockets().map { entities ->
                entities.map { it.toDomain() }
            }
        )
    }.onStart {
        // Refresh data from remote when the flow starts.
        refreshRockets()
    }

    private suspend fun refreshRockets() {
        try {
            val remoteRockets = remoteDataSource.fetchRockets()
            if (remoteRockets != null) {
                val entities = remoteRockets.map { it.toEntity() }
                localDataSource.clearRockets()
                localDataSource.upsertRockets(entities)
            }
        } catch (e: Exception) {
            // Handle error (logging, etc.)
            // In an offline-first app, we might just ignore the error and let the user see cached data.
        }
    }
}
