package com.dxmxp.data.remote.interceptor

import com.dxmxp.domain.AppException
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import javax.inject.Inject

class ErrorInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            throw AppException.NoInternetException(e)
        }

        if (!response.isSuccessful) {
            throw when(response.code){
                401 -> AppException.UnauthorizedException(response.message)
                404 -> AppException.NotFoundException(response.message)
                in 500..599 -> AppException.ServerException(response.code, response.message)
                else -> AppException.UnknownNetworkException(response.code, response.message)
            }
        }

        return response
    }

}