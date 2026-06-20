package com.dxmxp.data.repository

import android.util.Log
import com.dxmxp.data.common.PaginationHandler
import com.dxmxp.data.common.pagingFlow
import com.dxmxp.data.data_source.BeerRemoteDataSource
import com.dxmxp.data.repository.mapper.RocketMapper.toDomain
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
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    paginationHandlerFactory: PaginationHandler.Factory<Beer>
) : BeerRemoteRepository {

    private val paginationHandler = paginationHandlerFactory.create(pageSize = 20)

    override fun getBeers(shouldReset: Boolean): Flow<DataResult<List<Beer>?>> =
        DataResult.pagingFlow(
            dispatcher = ioDispatcher,
            paginationHandler = paginationHandler,
            shouldReset = shouldReset
        ) {
            remoteDataSource.fetchBeers(shouldReset)?.map { it.toDomain() }
        }.onEach {
            Log.d(TAG, "$it")
        }

    companion object {
        const val TAG = "BeerRepository"
    }
}
