package com.dxmxp.data.repository

import com.dxmxp.data.data_source.BeerLocalDataSource
import com.dxmxp.data.data_source.BeerRemoteDataSource
import com.dxmxp.data.repository.mapper.RocketMapper.toDomain
import com.dxmxp.data.repository.mapper.RocketMapper.toEntity
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import com.dxmxp.domain.model.Beer
import com.dxmxp.domain.repository.BeerRemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * Implementation of [BeerRemoteRepository] using an Offline-first strategy.
 * It uses [BeerLocalDataSource] as the Single Source of Truth (SSOT).
 */
class BeerRemoteRepositoryImpl @Inject constructor(
    private val remoteDataSource: BeerRemoteDataSource,
    private val localDataSource: BeerLocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BeerRemoteRepository {

    override fun getBeers(): Flow<List<Beer>> = flow {
        // Emit data from local database first, then try to refresh from remote.
        emitAll(
            localDataSource.getBeers().map { entities ->
                entities.map { it.toDomain() }
            }
        )
    }.onStart {
        // Refresh data from remote when the flow starts.
        refreshBeers()
    }.flowOn(ioDispatcher)

    private suspend fun refreshBeers() {
        try {
            val remoteRockets = remoteDataSource.fetchBeers()
            if (remoteRockets != null) {
                val entities = remoteRockets.map { it.toEntity() }
                localDataSource.clearBeers()
                localDataSource.upsertBeers(entities)
            }
        } catch (e: Exception) {
            // Handle error (logging, etc.)
            // In an offline-first app, we might just ignore the error and let the user see cached data.
        }
    }
}
