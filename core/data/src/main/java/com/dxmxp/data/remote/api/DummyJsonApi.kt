package com.dxmxp.data.remote.api

import com.dxmxp.data.remote.dto.auth.AuthRequest
import com.dxmxp.data.remote.dto.auth.AuthResponse
import com.dxmxp.data.remote.dto.auth.RefreshRequest
import com.dxmxp.data.remote.dto.auth.RefreshResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DummyJsonApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): RefreshResponse
}
