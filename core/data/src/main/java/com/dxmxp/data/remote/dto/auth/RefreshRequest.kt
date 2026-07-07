package com.dxmxp.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val refreshToken: String,
    val expiresInMins: Int = 60
)
