package com.dxmxp.data.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.dxmxp.domain.AppException
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkHandlerTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkHandler: NetworkHandler

    @Before
    fun setUp() {
        context = mockk()
        connectivityManager = mockk()
        networkHandler = NetworkHandler(context)

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
    }

    @Test
    fun `safeCall returns result when successful`() = runTest {
        // Given
        val expectedResult = "Success"

        // When
        val result = networkHandler.safeCall { expectedResult }

        // Then
        assertEquals(expectedResult, result)
    }

    @Test(expected = AppException.UnauthorizedException::class)
    fun `safeCall rethrows AppException`() = runTest {
        // When
        networkHandler.safeCall {
            throw AppException.UnauthorizedException()
        }
    }

    @Test
    fun `safeCall wraps SerializationException into UnknownException`() = runTest {
        // When
        try {
            networkHandler.safeCall {
                throw SerializationException("Parsing error")
            }
        } catch (e: AppException.UnknownException) {
            // Then
            assertEquals("Error parsing server response", e.message)
            assertTrue(e.cause is SerializationException)
        }
    }

    @Test
    fun `safeCall wraps generic Exception into UnknownException`() = runTest {
        // When
        try {
            networkHandler.safeCall {
                throw IllegalStateException("Something went wrong")
            }
        } catch (e: AppException.UnknownException) {
            // Then
            assertTrue(e.cause is IllegalStateException)
        }
    }

    @Test
    fun `hasInternetConnection returns true when WIFI is available`() {
        // Given
        val network: Network = mockk()
        val capabilities: NetworkCapabilities = mockk()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true

        // When
        val result = networkHandler.hasInternetConnection()

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasInternetConnection returns true when CELLULAR is available`() {
        // Given
        val network: Network = mockk()
        val capabilities: NetworkCapabilities = mockk()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true

        // When
        val result = networkHandler.hasInternetConnection()

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasInternetConnection returns false when no network is active`() {
        // Given
        every { connectivityManager.activeNetwork } returns null

        // When
        val result = networkHandler.hasInternetConnection()

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasInternetConnection returns false when no capabilities are found`() {
        // Given
        val network: Network = mockk()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns null

        // When
        val result = networkHandler.hasInternetConnection()

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasInternetConnection returns false when no supported transport is found`() {
        // Given
        val network: Network = mockk()
        val capabilities: NetworkCapabilities = mockk()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(any()) } returns false

        // When
        val result = networkHandler.hasInternetConnection()

        // Then
        assertFalse(result)
    }
}
