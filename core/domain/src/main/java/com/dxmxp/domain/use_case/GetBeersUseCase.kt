package com.dxmxp.domain.use_case

import com.dxmxp.domain.base.BaseFlowUseCase
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.model.Beer
import com.dxmxp.domain.repository.BeerRemoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case to retrieve the list of beers.
 * Returns domain models ([Beer]).
 */
class GetBeersUseCase @Inject constructor(
    private val repository: BeerRemoteRepository,
) : BaseFlowUseCase<Boolean, DataResult<List<Beer>?>>() {

    override fun launch(input: Boolean): Flow<DataResult<List<Beer>?>> =
        repository.getBeers(shouldReset = input)
}
