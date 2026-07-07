package com.dxmxp.domain.use_case

import com.dxmxp.domain.base.BaseFlowUseCase
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.model.AuthUser
import com.dxmxp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) : BaseFlowUseCase<LoginUseCase.Input, DataResult<AuthUser>>() {

    override fun launch(input: Input): Flow<DataResult<AuthUser>> =
        repository.login(input.username, input.password)

    data class Input(val username: String, val password: String)
}
