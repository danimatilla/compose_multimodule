package com.dxmxp.ui.navigation.helpers

import android.net.Uri
import android.util.Log
import com.dxmxp.ui.navigation.NavigationOrchestrator
import com.dxmxp.ui.navigation.RouteRegistry
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

    fun handle(uri: Uri): NavigationOrchestrator.NavigationEvent? {
        val screen = routeRegistry.createScreenFromUri(uri)

        return if (screen != null) {
            Log.d(TAG, "🔗 Deep link handled: ${uri.path} -> ${screen::class.simpleName}")
            NavigationOrchestrator.NavigationEvent.PushScreen(screen)
        } else {
            Log.w(TAG, "⚠️ Deep link not recognized: ${uri.path}")
            null
        }
    }

    private companion object {
        const val TAG = "DeepLinkHandler"
    }
}
