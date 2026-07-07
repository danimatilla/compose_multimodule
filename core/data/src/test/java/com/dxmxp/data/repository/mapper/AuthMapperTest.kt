package com.dxmxp.data.repository.mapper

import com.dxmxp.data.remote.dto.auth.AuthResponse
import com.dxmxp.data.repository.mapper.AuthMapper.toEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthMapperTest {

    @Test
    fun `toEntity should map AuthResponse to UserEntity correctly`() {
        val response = AuthResponse(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            gender = "male",
            image = "https://example.com/image.png",
            accessToken = "access",
            refreshToken = "refresh"
        )

        val entity = response.toEntity()

        assertEquals(response.id, entity.id)
        assertEquals(response.username, entity.username)
        assertEquals(response.email, entity.email)
        assertEquals(response.firstName, entity.firstName)
        assertEquals(response.lastName, entity.lastName)
        assertEquals(response.gender, entity.gender)
        assertEquals(response.image, entity.image)
    }
}
