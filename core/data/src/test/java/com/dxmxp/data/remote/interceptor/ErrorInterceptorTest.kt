package com.dxmxp.data.remote.interceptor

import com.dxmxp.domain.AppException
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ErrorInterceptorTest {

    private lateinit var interceptor: ErrorInterceptor
    private lateinit var chain: Interceptor.Chain
    private lateinit var request: Request

    @Before
    fun setUp() {
        interceptor = ErrorInterceptor()
        chain = mockk()
        request = Request.Builder().url("https://test.com").build()
        every { chain.request() } returns request
    }

    @Test
    fun `intercept returns response when successful`() {
        // Given
        val mockResponse = createResponse(200, "OK")
        every { chain.proceed(any()) } returns mockResponse

        // When
        val response = interceptor.intercept(chain)

        // Then
        assertEquals(mockResponse, response)
    }

    @Test(expected = AppException.NoInternetException::class)
    fun `intercept throws NoInternetException when IOException occurs`() {
        // Given
        every { chain.proceed(any()) } throws IOException("No connection")

        // When
        interceptor.intercept(chain)
    }

    @Test
    fun `intercept throws UnauthorizedException when status code is 401`() {
        // Given
        val mockResponse = createResponse(401, "Unauthorized")
        every { chain.proceed(any()) } returns mockResponse

        // When
        try {
            interceptor.intercept(chain)
        } catch (e: Exception) {
            // Then
            assertTrue(e is AppException.UnauthorizedException)
            assertEquals("Unauthorized", e.message)
        }
    }

    @Test
    fun `intercept throws NotFoundException when status code is 404`() {
        // Given
        val mockResponse = createResponse(404, "Not Found")
        every { chain.proceed(any()) } returns mockResponse

        // When
        try {
            interceptor.intercept(chain)
        } catch (e: Exception) {
            // Then
            assertTrue(e is AppException.NotFoundException)
            assertEquals("Not Found", e.message)
        }
    }

    @Test
    fun `intercept throws ServerException when status code is 5xx`() {
        // Given
        val mockResponse = createResponse(500, "Internal Server Error")
        every { chain.proceed(any()) } returns mockResponse

        // When
        try {
            interceptor.intercept(chain)
        } catch (e: Exception) {
            // Then
            assertTrue(e is AppException.ServerException)
            val serverException = e as AppException.ServerException
            assertEquals(500, serverException.code)
            assertEquals("Internal Server Error", serverException.message)
        }
    }

    @Test
    fun `intercept throws UnknownNetworkException for other unsuccessful status codes`() {
        // Given
        val mockResponse = createResponse(403, "Forbidden")
        every { chain.proceed(any()) } returns mockResponse

        // When
        try {
            interceptor.intercept(chain)
        } catch (e: Exception) {
            // Then
            assertTrue(e is AppException.UnknownNetworkException)
            val unknownException = e as AppException.UnknownNetworkException
            assertEquals(403, unknownException.code)
            assertEquals("Forbidden", unknownException.message)
        }
    }

    private fun createResponse(code: Int, message: String): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(message)
            .build()
    }
}
