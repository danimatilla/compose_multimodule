package com.dxmxp.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val username: String,
    val password: String,
    val expiresInMins: Int? = null
)
