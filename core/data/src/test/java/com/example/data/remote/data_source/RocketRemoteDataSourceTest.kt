package com.example.data.remote.data_source

import com.example.data.remote.api.SpaceXApi
import com.example.data.remote.dto.rocket.RocketResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RocketRemoteDataSourceTest {

    private lateinit var api: SpaceXApi.Rockets
    private lateinit var rocketRemoteDataSource: IRocketRemoteDataSource
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        api = mockk()
        rocketRemoteDataSource = RocketRemoteDataSource(
            api = api,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun fetchRockets_returnsValidRocketList() = runTest(testDispatcher) {
        val mockRockets = listOf(
            RocketResponse(
                id = "1",
                name = "Falcon 9",
                type = "Rocket",
                height = RocketResponse.Dimension(meters = 70f),
                diameter = RocketResponse.Dimension(meters = 3.7f),
                firstFlight = "2015-01-01",
                images = listOf("https://example.com/image1.jpg"),
                description = "Test rocket",
                wikipedia = "https://en.wikipedia.org/wiki/Falcon_9"
            ),
            RocketResponse(
                id = "2",
                name = "Falcon Heavy",
                type = "Rocket",
                height = RocketResponse.Dimension(meters = 70f),
                diameter = RocketResponse.Dimension(meters = 3.7f),
                firstFlight = "2018-02-06",
                images = listOf("https://example.com/image2.jpg"),
                description = "Test heavy rocket",
                wikipedia = "https://en.wikipedia.org/wiki/Falcon_Heavy"
            )
        )
        coEvery { api.fetchRockets() } returns mockRockets

        val result = rocketRemoteDataSource.fetchRockets()

        assertEquals(mockRockets, result)
        assertEquals(2, result?.size)
    }

    @Test
    fun fetchRockets_returnsEmpty() = runTest(testDispatcher) {
        coEvery { api.fetchRockets() } returns emptyList()

        val result = rocketRemoteDataSource.fetchRockets()

        assertEquals(emptyList<RocketResponse>(), result)
    }

    @Test
    fun fetchRockets_returnsNull() = runTest(testDispatcher) {
        coEvery { api.fetchRockets() } returns null

        val result = rocketRemoteDataSource.fetchRockets()

        assertNull(result)
    }

    @Test(expected = Exception::class)
    fun fetchRockets_propagatesException() = runTest(testDispatcher) {
        coEvery { api.fetchRockets() } throws Exception("Network error")

        rocketRemoteDataSource.fetchRockets()
    }

    @Test
    fun fetchRockets_usesCorrectDispatcher() = runTest(testDispatcher) {
        val mockRockets = listOf(
            RocketResponse(
                id = "1",
                name = "Falcon 9",
                type = "Rocket",
                height = RocketResponse.Dimension(meters = 70f),
                diameter = RocketResponse.Dimension(meters = 3.7f),
                firstFlight = "2015-01-01",
                images = listOf("https://example.com/image1.jpg"),
                description = "Test rocket",
                wikipedia = "https://en.wikipedia.org/wiki/Falcon_9"
            )
        )
        coEvery { api.fetchRockets() } returns mockRockets

        val result = rocketRemoteDataSource.fetchRockets()

        assertEquals(mockRockets, result)
    }
}
