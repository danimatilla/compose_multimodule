package com.dxmxp.seed.use_case

import com.dxmxp.domain.base.BaseFlowUseCase
import com.dxmxp.domain.model.Beer
import com.dxmxp.domain.repository.BeerRemoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve the list of rockets.
 */
class GetBeersUseCase @Inject constructor(
    private val repository: BeerRemoteRepository,
) : BaseFlowUseCase<Unit, List<Beer>>() {

    override fun launch(params: Unit): Flow<List<Beer>> =
        repository.getBeers()
}