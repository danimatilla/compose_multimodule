package com.dxmxp.data.common

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHandler @Inject constructor() {

    suspend fun <T> safeCall(call: suspend () -> T): T =
        try {
            call()
        } catch (e: Exception) {
            // Log the error centrally (Sentry, Firebase, etc.)
            // logError(e)

            throw e
        }
}
