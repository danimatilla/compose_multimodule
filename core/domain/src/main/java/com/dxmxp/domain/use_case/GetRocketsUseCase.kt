package com.dxmxp.domain.use_case

import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import com.dxmxp.domain.model.Rocket
import com.dxmxp.domain.repository.RocketRemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve the list of rockets.
 * Implementation using [BaseFlowUseCase] to standardize dispatcher handling.
 */
class GetRocketsUseCase @Inject constructor(
    private val repository: RocketRemoteRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseFlowUseCase<Unit, List<Rocket>>(dispatcher) {

    override fun launch(params: Unit): Flow<List<Rocket>> = repository.getRockets()
}
