package com.dxmxp.data.data_source

import com.dxmxp.data.common.NetworkHandler
import com.dxmxp.data.common.Paginator
import com.dxmxp.data.remote.api.SpaceXApi
import com.dxmxp.data.remote.dto.rocket.RocketResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RocketRemoteDataSourceTest {

    private lateinit var api: SpaceXApi.Rockets
    private lateinit var paginator: Paginator<Int, List<RocketResponse>>
    private lateinit var networkHandler: NetworkHandler
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var dataSource: RocketRemoteDataSource

    @Before
    fun setUp() {
        api = mockk()
        paginator = mockk()
        networkHandler = mockk()

        dataSource = RocketRemoteDataSource(
            api = api,
            paginator = paginator,
            networkHandler = networkHandler,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `fetchRockets calls safeCall and fetchOneShot and returns data`() = runTest {
        // Given
        val mockRockets = listOf(mockk<RocketResponse>(), mockk<RocketResponse>())

        // Mock safeCall to execute the block it receives
        coEvery { networkHandler.safeCall<List<RocketResponse>?>(any()) } coAnswers {
            val block = firstArg<suspend () -> List<RocketResponse>?>()
            block()
        }

        // Mock fetchOneShot to execute the fetch block it receives
        coEvery { paginator.fetchOneShot(any()) } coAnswers {
            val fetchBlock = firstArg<suspend () -> List<RocketResponse>?>()
            fetchBlock()
        }

        coEvery { api.fetchRockets() } returns mockRockets

        // When
        val result = dataSource.fetchRockets()

        // Then
        assertEquals(mockRockets, result)
        coVerify(exactly = 1) { networkHandler.safeCall<List<RocketResponse>?>(any()) }
        coVerify(exactly = 1) { paginator.fetchOneShot(any()) }
        coVerify(exactly = 1) { api.fetchRockets() }
    }

    @Test
    fun `fetchRockets returns null when API returns null`() = runTest {
        // Given
        coEvery { networkHandler.safeCall<List<RocketResponse>?>(any()) } coAnswers {
            val block = firstArg<suspend () -> List<RocketResponse>?>()
            block()
        }
        coEvery { paginator.fetchOneShot(any()) } coAnswers {
            val fetchBlock = firstArg<suspend () -> List<RocketResponse>?>()
            fetchBlock()
        }
        coEvery { api.fetchRockets() } returns null

        // When
        val result = dataSource.fetchRockets()

        // Then
        assertNull(result)
    }

    @Test(expected = Exception::class)
    fun `fetchRockets propagates exception from safeCall`() = runTest {
        // Given
        coEvery { networkHandler.safeCall<List<RocketResponse>?>(any()) } throws Exception("Network Error")

        // When
        dataSource.fetchRockets()

        // Then -> Exception expected
    }
}
