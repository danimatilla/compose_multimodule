package com.dxmxp.domain


sealed class AppException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    // Errores de Conexión (normalmente envuelven una IOException)
    class NoInternetException(cause: Throwable) : AppException("No internet connection", cause)

    // Errores de Autorización (401)
    class UnauthorizedException(message: String = "Unauthorized access") : AppException(message)

    // Recurso no encontrado (404)
    class NotFoundException(message: String = "Resource not found") : AppException(message)

    // Errores de Servidor (5xx)
    class ServerException(
        val code: Int,
        message: String = "Server error: $code",
        cause: Throwable? = null
    ) : AppException(message, cause)

    // Errores de red desconocidos
    class UnknownNetworkException(val code: Int, message: String = "Unknown network error: $code") :
        AppException(message)

    // Error genérico de la aplicación
    class UnknownException(
        message: String = "An unknown error occurred",
        cause: Throwable? = null
    ) : AppException(message, cause)
}
