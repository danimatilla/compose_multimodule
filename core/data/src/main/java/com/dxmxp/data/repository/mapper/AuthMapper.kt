package com.dxmxp.data.repository.mapper

import com.dxmxp.data.local.entity.UserEntity
import com.dxmxp.data.remote.dto.auth.AuthResponse
import com.dxmxp.domain.model.AuthUser

object AuthMapper {
    fun AuthResponse.toDomain(): AuthUser = AuthUser(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        gender = gender,
        image = image,
        accessToken = accessToken,
        refreshToken = refreshToken
    )

    fun AuthResponse.toEntity(): UserEntity = UserEntity(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        gender = gender,
        image = image
    )
}
