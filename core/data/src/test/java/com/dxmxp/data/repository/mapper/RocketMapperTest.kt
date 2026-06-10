package com.dxmxp.data.repository.mapper

import com.dxmxp.data.remote.dto.rocket.RocketResponse
import com.dxmxp.data.repository.mapper.RocketMapper.toDomain
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class RocketMapperTest {

    @Test
    fun `toDomain maps RocketResponse to Rocket correctly when meters is present`() {
        // Given
        val mockRocketResponse = mockk<RocketResponse> {
            every { id } returns "rocket-1"
            every { name } returns "Falcon 1"
            every { firstFlight } returns "2006-03-24"
            every { flickrImages } returns listOf("img1", "img2")
            every { height } returns RocketResponse.Dimension(meters = 22.25f, feet = 73f)
        }

        // When
        val result = mockRocketResponse.toDomain()

        // Then
        assertEquals("rocket-1", result.id)
        assertEquals("Falcon 1", result.name)
        assertEquals(22.25f, result.height)
        assertEquals("2006-03-24", result.firstFlight)
        assertEquals(listOf("img1", "img2"), result.images)
    }

    @Test
    fun `toDomain maps height correctly when meters is null and feet is present`() {
        // Given
        val mockRocketResponse = mockk<RocketResponse> {
            every { id } returns "rocket-1"
            every { name } returns "Falcon 1"
            every { firstFlight } returns "2006-03-24"
            every { flickrImages } returns listOf("img1")
            every { height } returns RocketResponse.Dimension(meters = null, feet = 100f)
        }

        // When
        val result = mockRocketResponse.toDomain()

        // Then
        assertEquals(30.48f, result.height, 0.0001f)
    }

    @Test
    fun `toDomain maps height to 0 when both meters and feet are null`() {
        // Given
        val mockRocketResponse = mockk<RocketResponse> {
            every { id } returns "rocket-1"
            every { name } returns "Falcon 1"
            every { firstFlight } returns "2006-03-24"
            every { flickrImages } returns listOf("img1")
            every { height } returns RocketResponse.Dimension(meters = null, feet = null)
        }

        // When
        val result = mockRocketResponse.toDomain()

        // Then
        assertEquals(0f, result.height)
    }
}
