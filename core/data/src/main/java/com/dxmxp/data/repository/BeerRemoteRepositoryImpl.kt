package com.dxmxp.data.repository

import com.dxmxp.data.common.pagingFlow
import com.dxmxp.data.data_source.BeerRemoteDataSource
import com.dxmxp.data.repository.mapper.BeerMapper.toDomain
import com.dxmxp.domain.base.Logger
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import com.dxmxp.domain.model.Beer
import com.dxmxp.domain.repository.BeerRemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [BeerRemoteRepository] fetching directly from remote.
 */
@Singleton
class BeerRemoteRepositoryImpl @Inject constructor(
    private val remoteDataSource: BeerRemoteDataSource,
    private val logger: Logger,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BeerRemoteRepository {

    override fun getBeers(shouldReset: Boolean): Flow<DataResult<List<Beer>?>> =
        DataResult.pagingFlow(dispatcher = ioDispatcher) {
            remoteDataSource.fetchBeers(shouldReset)?.map { it.toDomain() }
        }.onEach {result ->
            when (result) {
                is DataResult.Success -> logger.d(TAG, "$result")
                is DataResult.Error -> logger.e(TAG, "Error: ${result.exception}")
                is DataResult.Loading -> logger.d(TAG, "Loading...")
            }
        }

    companion object {
        const val TAG = "BeerRepository"
    }
}
