package com.dxmxp.data.repository

import android.util.Log
import com.dxmxp.data.data_source.BeerRemoteDataSource
import com.dxmxp.data.repository.mapper.RocketMapper.toDomain
import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.common.PaginationConfig.DEFAULT_PAGE_SIZE
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import com.dxmxp.domain.model.Beer
import com.dxmxp.domain.repository.BeerRemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [BeerRemoteRepository] fetching directly from remote.
 */
@Singleton
class BeerRemoteRepositoryImpl @Inject constructor(
    private val remoteDataSource: BeerRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BeerRemoteRepository {

    private val cachedBeers = mutableListOf<Beer>()
    private var isEndReached = false

    override fun getBeers(shouldReset: Boolean): Flow<DataResult<List<Beer>?>> = flow {
        emit(DataResult.Loading)

        if (shouldReset) {
            cachedBeers.clear()
            isEndReached = false
        }

        val response = remoteDataSource.fetchBeers(shouldReset)
        val newBeers = response?.map { it.toDomain() }

        if (newBeers != null) {
            cachedBeers.addAll(newBeers)
            isEndReached = newBeers.size < DEFAULT_PAGE_SIZE
        } else {
            isEndReached = true
        }

        val resultData = if (cachedBeers.isEmpty() && newBeers == null) null else cachedBeers.toList()

        emit(DataResult.Success(data = resultData, endReached = isEndReached))
    }.catch { e ->
        val appException = e as? AppException ?: AppException.UnknownException(cause = e)
        emit(DataResult.Error(appException))
    }.onEach {
        Log.d(TAG, "$it")
    }.flowOn(ioDispatcher)

    companion object {
        const val TAG = "BeerRepository"
    }
}
