package com.dxmxp.data.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.dxmxp.domain.AppException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerializationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHandler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    /**
     * Executes a network call safely.
     * Network exceptions (4xx, 5xx, No Internet) are already handled by the ErrorInterceptor,
     * but here we catch unexpected errors such as serialization failures or logic errors.
     */
    suspend fun <T> safeCall(call: suspend () -> T): T =
        try {
            call()
        } catch (e: AppException) {
            // If it's already an AppException (thrown by the Interceptor), rethrow it as is
            throw e
        } catch (e: SerializationException) {
            // Error parsing the JSON
            throw AppException.UnknownException("Error parsing server response", e)
        } catch (e: Exception) {
            // Any other unexpected error (NPE, etc.)
            // You could add centralized logging like Sentry or Firebase here
            throw AppException.UnknownException(cause = e)
        }

    /**
     * Checks if the device has an active internet connection.
     */
    fun hasInternetConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
    }
}
