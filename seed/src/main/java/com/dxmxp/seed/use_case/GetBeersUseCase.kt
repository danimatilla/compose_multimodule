package com.dxmxp.seed.use_case

import com.dxmxp.domain.base.BaseFlowUseCase
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.model.Beer
import com.dxmxp.domain.repository.BeerRemoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve the list of beers.
 */
class GetBeersUseCase @Inject constructor(
    private val repository: BeerRemoteRepository,
) : BaseFlowUseCase<Boolean, DataResult<List<Beer>?>>() {

    override fun launch(params: Boolean): Flow<DataResult<List<Beer>?>> =
        repository.getBeers(isNextPage = params)
}
