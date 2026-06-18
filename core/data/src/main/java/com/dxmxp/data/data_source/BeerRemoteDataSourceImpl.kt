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
    suspend fun fetchBeers(shouldReset: Boolean = false): List<BeerResponse>?
}

class BeerRemoteDataSourceImpl @Inject constructor(
    private val api: PunkapiApi.Beers,
    private val networkHandler: NetworkHandler,
    @param:PaginatorByPage private val paginator: Paginator<Int, List<BeerResponse>>,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : BeerRemoteDataSource {

    override suspend fun fetchBeers(shouldReset: Boolean): List<BeerResponse>? {
        return withContext(dispatcher) {
            networkHandler.safeCall {
                if (shouldReset) {
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
