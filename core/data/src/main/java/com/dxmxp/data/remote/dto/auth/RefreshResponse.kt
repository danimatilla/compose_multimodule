package com.dxmxp.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String
)
