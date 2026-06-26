package com.dxmxp.data.data_source

import com.dxmxp.data.common.NetworkHandler
import com.dxmxp.data.remote.api.DummyJsonApi
import com.dxmxp.data.remote.dto.auth.AuthRequest
import com.dxmxp.data.remote.dto.auth.AuthResponse
import com.dxmxp.domain.di.DispatchersModule.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AuthRemoteDataSource {
    suspend fun login(request: AuthRequest): AuthResponse
}

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: DummyJsonApi,
    private val networkHandler: NetworkHandler,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : AuthRemoteDataSource {

    override suspend fun login(request: AuthRequest): AuthResponse =
        withContext(dispatcher) {
            networkHandler.safeCall {
                api.login(request)
            }
        }
}
