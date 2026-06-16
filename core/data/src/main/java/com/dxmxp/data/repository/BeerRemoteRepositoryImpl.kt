package com.dxmxp.data.repository

import com.dxmxp.data.common.loadingFlow
import com.dxmxp.data.data_source.BeerRemoteDataSource
import com.dxmxp.data.repository.mapper.RocketMapper.toDomain
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import com.dxmxp.domain.model.Beer
import com.dxmxp.domain.repository.BeerRemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [BeerRemoteRepository] fetching directly from remote.
 */
class BeerRemoteRepositoryImpl @Inject constructor(
    private val remoteDataSource: BeerRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BeerRemoteRepository {

    override fun getBeers(): Flow<DataResult<List<Beer>?>> =
        DataResult.loadingFlow(ioDispatcher) {
            val remoteBeers = remoteDataSource.fetchBeers()
            remoteBeers?.map { it.toDomain() }
        }
}
