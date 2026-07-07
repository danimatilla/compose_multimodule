package com.dxmxp.domain

import java.io.IOException


sealed class AppException(message: String? = null, cause: Throwable? = null) : IOException(message, cause) {
    // Connection Errors (normally wrapping an IOException)
    class NoInternetException(cause: Throwable) : AppException("No internet connection", cause)

    // Authorization Errors (401)
    class UnauthorizedException(message: String = "Unauthorized access") : AppException(message)

    // Bad Request (400)
    class BadRequestException(message: String = "Bad Request") : AppException(message)

    // Resource not found (404)
    class NotFoundException(message: String = "Resource not found") : AppException(message)

    // Server Errors (5xx)
    class ServerException(
        val code: Int,
        message: String = "Server error: $code",
        cause: Throwable? = null
    ) : AppException(message, cause)

    // Unknown network errors
    class UnknownNetworkException(val code: Int, message: String = "Unknown network error: $code") :
        AppException(message)

    // Generic application error
    class UnknownException(
        message: String = "An unknown error occurred",
        cause: Throwable? = null
    ) : AppException(message, cause)
}
