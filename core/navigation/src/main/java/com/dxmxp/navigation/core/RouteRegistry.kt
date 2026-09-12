package com.dxmxp.navigation.core

import android.net.Uri
import com.dxmxp.domain.base.Logger
import com.dxmxp.navigation.model.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simplified registry for route lookup, primarily for Deep Linking.
 */
@Singleton
class RouteRegistry @Inject constructor(
    private val logger: Logger
) {
    private val routeCreators = mutableMapOf<String, (Uri) -> Route?>()

    /**
     * Registers a route pattern and its creator function.
     */
    fun register(pattern: String, creator: (Uri) -> Route?) {
        routeCreators[pattern] = creator
    }

    /**
     * Creates a route instance from a deep link URI.
     */
    fun createRouteFromUri(uri: Uri): Route? {
        val path = uri.path?.removeSuffix("/") ?: return null
        
        // Find a matching pattern (this is a simple implementation, can be improved)
        for ((pattern, creator) in routeCreators) {
            if (path.startsWith(pattern)) {
                val route = creator(uri)
                if (route != null) {
                    logger.d(TAG, "🔗 Deep Link Match: $path -> ${route::class.simpleName}")
                    return route
                }
            }
        }
        
        logger.w(TAG, "⚠️ Deep Link No Match: $path")
        return null
    }

    private companion object {
        const val TAG = "RouteRegistry"
    }
}
