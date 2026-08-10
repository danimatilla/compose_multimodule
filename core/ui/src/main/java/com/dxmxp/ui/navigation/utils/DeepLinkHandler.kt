package com.dxmxp.ui.navigation.utils

import android.net.Uri
import android.util.Log
import com.dxmxp.ui.navigation.core.NavigationEvent
import com.dxmxp.ui.navigation.core.RouteRegistry
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles deep link resolution using the centralized RouteRegistry.
 * No more reflection - routes are pre-registered and validated at startup.
 */
@Singleton
class DeepLinkHandler @Inject constructor(
    private val routeRegistry: RouteRegistry
) {

    fun handle(uri: Uri): NavigationEvent? {
        val route = routeRegistry.createRouteFromUri(uri)

        return if (route != null) {
            Log.d(TAG, "🔗 Deep link handled: ${uri.path} -> ${route::class.simpleName}")
            NavigationEvent.PushScreen(route)
        } else {
            Log.w(TAG, "⚠️ Deep link not recognized: ${uri.path}")
            null
        }
    }

    private companion object {
        const val TAG = "DeepLinkHandler"
    }
}
