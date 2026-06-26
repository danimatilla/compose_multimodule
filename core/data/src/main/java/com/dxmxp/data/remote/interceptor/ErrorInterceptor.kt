package com.dxmxp.data.remote.interceptor

import com.dxmxp.data.remote.dto.error.BaseErrorResponse
import com.dxmxp.domain.AppException
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import javax.inject.Inject

class ErrorInterceptor @Inject constructor(): Interceptor {

    private val jsonParser = Json { ignoreUnknownKeys = true }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            if (e is AppException) throw e
            throw AppException.NoInternetException(e)
        }

        if (!response.isSuccessful) {
            val errorBody = try {
                response.peekBody(1024).string()
            } catch (e: Exception) {
                null
            }

            val errorMessage = parseErrorMessage(errorBody) ?: response.message

            throw when(response.code){
                400 -> AppException.BadRequestException(errorMessage)
                401 -> AppException.UnauthorizedException(errorMessage)
                404 -> AppException.NotFoundException(errorMessage)
                in 500..599 -> AppException.ServerException(response.code, errorMessage)
                else -> AppException.UnknownNetworkException(response.code, errorMessage)
            }
        }

        return response
    }

    private fun parseErrorMessage(json: String?): String? {
        if (json.isNullOrBlank()) return null
        return try {
            val errorResponse = jsonParser.decodeFromString<BaseErrorResponse>(json)
            errorResponse.getAnyMessage()
        } catch (e: Exception) {
            null
        }
    }
}
