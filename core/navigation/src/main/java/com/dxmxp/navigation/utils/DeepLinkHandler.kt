package com.dxmxp.navigation.utils

import android.net.Uri
import com.dxmxp.domain.base.Logger
import com.dxmxp.navigation.core.RouteRegistry
import com.dxmxp.navigation.model.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles incoming Deep Links and maps them to Routes.
 */
@Singleton
class DeepLinkHandler @Inject constructor(
    private val routeRegistry: RouteRegistry,
    private val logger: Logger
) {

    fun handle(uri: Uri): Route? {
        logger.d(TAG, "Incoming Deep Link: $uri")
        return routeRegistry.createRouteFromUri(uri)
    }

    private companion object {
        const val TAG = "DeepLinkHandler"
    }
}
