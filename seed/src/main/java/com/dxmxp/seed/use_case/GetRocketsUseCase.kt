package com.dxmxp.seed.use_case

import com.dxmxp.domain.base.BaseFlowUseCase
import com.dxmxp.domain.model.Rocket
import com.dxmxp.domain.repository.RocketRemoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve the list of rockets.
 */
class GetRocketsUseCase @Inject constructor(
    private val repository: RocketRemoteRepository,
) : BaseFlowUseCase<Unit, List<Rocket>>() {

    override fun launch(params: Unit): Flow<List<Rocket>> =
        repository.getRockets()
}