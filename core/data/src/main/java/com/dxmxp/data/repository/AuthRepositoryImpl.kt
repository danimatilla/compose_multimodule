package com.dxmxp.data.repository

import com.dxmxp.data.SessionManager
import com.dxmxp.data.data_source.AuthRemoteDataSource
import com.dxmxp.data.common.loadingFlow
import com.dxmxp.data.remote.dto.auth.AuthRequest
import com.dxmxp.data.repository.mapper.AuthMapper.toDomain
import com.dxmxp.domain.base.Logger
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import com.dxmxp.domain.model.AuthUser
import com.dxmxp.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [AuthRepository] fetching from remote.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager,
    private val logger: Logger,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    override fun login(username: String, password: String): Flow<DataResult<AuthUser>> =
        DataResult.loadingFlow(dispatcher = ioDispatcher) {
            val response = remoteDataSource.login(AuthRequest(username, password))
            sessionManager.saveToken(response.accessToken)
            response.toDomain()
        }.onEach { result ->
            when (result) {
                is DataResult.Success -> logger.d(TAG, "Login success: ${result.data}")
                is DataResult.Error -> logger.e(TAG, "Login error: ${result.exception}")
                is DataResult.Loading -> logger.d(TAG, "Login loading...")
            }
        }

    override fun logout() {
        sessionManager.clearSession()
        logger.d(TAG, "User logged out")
    }

    override fun getAccessToken(): String? = sessionManager.accessToken

    companion object {
        const val TAG = "AuthRepository"
    }
}
