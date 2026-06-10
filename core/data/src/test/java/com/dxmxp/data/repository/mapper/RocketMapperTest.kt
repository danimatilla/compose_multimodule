package com.dxmxp.data.repository.mapper

import com.dxmxp.data.local.entity.RocketEntity
import com.dxmxp.data.remote.dto.rocket.RocketResponse
import com.dxmxp.data.repository.mapper.RocketMapper.toDomain
import com.dxmxp.data.repository.mapper.RocketMapper.toEntity
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class RocketMapperTest {

    @Test
    fun `toDomain maps RocketResponse to Rocket correctly`() {
        // ... (existing test code)
    }

    // ... (existing tests)

    @Test
    fun `toEntity maps RocketResponse to RocketEntity correctly`() {
        // Given
        val mockRocketResponse = mockk<RocketResponse> {
            every { id } returns "rocket-1"
            every { name } returns "Falcon 1"
            every { type } returns "rocket"
            every { firstFlight } returns "2006-03-24"
            every { flickrImages } returns listOf("img1", "img2")
            every { height } returns RocketResponse.Dimension(meters = 22.25f, feet = 73f)
            every { diameter } returns RocketResponse.Dimension(meters = 1.7f, feet = 5.5f)
            every { wikipedia } returns "https://wikipedia.org"
            every { description } returns "Rocket description"
        }

        // When
        val result = mockRocketResponse.toEntity()

        // Then
        assertEquals("rocket-1", result.id)
        assertEquals("Falcon 1", result.name)
        assertEquals("rocket", result.type)
        assertEquals(22.25f, result.height)
        assertEquals(1.7f, result.diameter)
        assertEquals("img1", result.image)
        assertEquals("2006-03-24", result.firstFlight)
        assertEquals("https://wikipedia.org", result.wikipedia)
        assertEquals("Rocket description", result.description)
    }

    @Test
    fun `toDomain maps RocketEntity to Rocket correctly`() {
        // Given
        val entity = RocketEntity(
            id = "rocket-1",
            name = "Falcon 1",
            type = "rocket",
            height = 22.25f,
            diameter = 1.7f,
            image = "img1",
            firstFlight = "2006-03-24",
            wikipedia = "https://wikipedia.org",
            description = "Rocket description"
        )

        // When
        val result = entity.toDomain()

        // Then
        assertEquals("rocket-1", result.id)
        assertEquals("Falcon 1", result.name)
        assertEquals(22.25f, result.height)
        assertEquals("2006-03-24", result.firstFlight)
        assertEquals(listOf("img1"), result.images)
    }
}
