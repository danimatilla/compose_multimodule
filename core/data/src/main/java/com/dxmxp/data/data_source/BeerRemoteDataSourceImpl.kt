package com.dxmxp.data.data_source

import com.dxmxp.data.common.NetworkHandler
import com.dxmxp.data.common.Paginator
import com.dxmxp.data.di.PaginatorModule.PaginatorByPage
import com.dxmxp.data.remote.api.PunkapiApi
import com.dxmxp.data.remote.dto.rocket.BeerResponse
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface BeerRemoteDataSource {
    suspend fun fetchBeers(isNextPage: Boolean = false): List<BeerResponse>?
}

class BeerRemoteDataSourceImpl @Inject constructor(
    private val api: PunkapiApi.Beers,
    @param:PaginatorByPage private val paginator: Paginator<Int, List<BeerResponse>>,
    private val networkHandler: NetworkHandler,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : BeerRemoteDataSource {

    override suspend fun fetchBeers(isNextPage: Boolean): List<BeerResponse>? {
        return withContext(dispatcher) {
            networkHandler.safeCall {
                if (!isNextPage) {
                    paginator.reset()
                }
                paginator.fetchPaged { key, limit ->
                    api.fetchBeers(
                        page = key,
                        limit = limit
                    )
                }
            }
        }
    }
}
