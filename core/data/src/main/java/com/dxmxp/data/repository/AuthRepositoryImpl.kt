package com.dxmxp.data.repository

import com.dxmxp.data.SessionManager
import com.dxmxp.data.data_source.AuthRemoteDataSource
import com.dxmxp.data.common.loadingFlow
import com.dxmxp.data.local.dao.UserDao
import com.dxmxp.data.local.datastore.SessionDataStore
import com.dxmxp.data.remote.dto.auth.AuthRequest
import com.dxmxp.data.remote.dto.auth.RefreshRequest
import com.dxmxp.data.repository.mapper.AuthMapper.toDomain
import com.dxmxp.data.repository.mapper.AuthMapper.toEntity
import com.dxmxp.domain.base.Logger
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import com.dxmxp.domain.model.AuthUser
import com.dxmxp.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    private val userDao: UserDao,
    private val sessionDataStore: SessionDataStore,
    private val logger: Logger,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    override fun login(username: String, password: String): Flow<DataResult<AuthUser>> =
        DataResult.loadingFlow(dispatcher = ioDispatcher) {
            val response = remoteDataSource.login(AuthRequest(username, password))
            
            // Save to in-memory session
            sessionManager.saveToken(response.accessToken)
            
            // Save user to DB (no tokens)
            userDao.upsertUser(response.toEntity())
            
            // Save tokens to encrypted DataStore
            sessionDataStore.saveTokens(response.accessToken, response.refreshToken)
            
            response.toDomain()
        }.onEach { result ->
            when (result) {
                is DataResult.Success -> logger.d(TAG, "Login success: ${result.data}")
                is DataResult.Error -> logger.e(TAG, "Login error: ${result.exception.message}")
                is DataResult.Loading -> logger.d(TAG, "Login loading...")
            }
        }

    override fun autoLogin(): Flow<DataResult<Unit>> =
        DataResult.loadingFlow(dispatcher = ioDispatcher) {
            val refreshToken = sessionDataStore.refreshToken.first()
            if (refreshToken != null) {
                val response = remoteDataSource.refresh(RefreshRequest(refreshToken))
                sessionManager.saveToken(response.accessToken)
                sessionDataStore.saveTokens(response.accessToken, response.refreshToken)
            } else {
                throw Exception("No session found")
            }
        }.onEach { result ->
            when (result) {
                is DataResult.Success -> logger.d(TAG, "Auto-login success")
                is DataResult.Error -> logger.e(TAG, "Auto-login error: ${result.exception.message}")
                is DataResult.Loading -> logger.d(TAG, "Auto-login loading...")
            }
        }

    override suspend fun logout() {
        sessionManager.clearSession()
        userDao.deleteUser()
        sessionDataStore.clearSession()
        logger.d(TAG, "User logged out")
    }

    override fun getAccessToken(): String? = sessionManager.accessToken

    companion object {
        const val TAG = "AuthRepository"
    }
}
