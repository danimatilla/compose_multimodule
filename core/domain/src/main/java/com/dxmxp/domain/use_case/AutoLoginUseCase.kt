package com.dxmxp.domain.use_case

import com.dxmxp.domain.base.BaseFlowUseCase
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AutoLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) : BaseFlowUseCase<Unit, DataResult<Unit>>() {

    override fun launch(input: Unit): Flow<DataResult<Unit>> =
        repository.autoLogin()
}
