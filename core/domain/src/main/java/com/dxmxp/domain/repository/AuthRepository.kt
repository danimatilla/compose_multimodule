package com.dxmxp.domain.repository

import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.model.AuthUser

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(username: String, password: String): Flow<DataResult<AuthUser>>
    fun logout()
    fun getAccessToken(): String?
}
