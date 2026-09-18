package com.dxmxp.domain.use_case

import com.dxmxp.domain.base.BaseUseCase
import com.dxmxp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case to check if there is an active session (access token exists).
 */
class HasSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : BaseUseCase<Unit, Boolean>() {

    override suspend fun launch(input: Unit): Boolean {
        return authRepository.hasSession()
    }
}
