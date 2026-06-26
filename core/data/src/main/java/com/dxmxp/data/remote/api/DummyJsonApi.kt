package com.dxmxp.data.remote.api

import com.dxmxp.data.remote.dto.auth.AuthRequest
import com.dxmxp.data.remote.dto.auth.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DummyJsonApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): AuthResponse
}
