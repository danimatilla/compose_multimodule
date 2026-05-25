package com.dxmxp.ui.navigation.helpers

import android.net.Uri
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.Screen
import javax.inject.Inject

class DeepLinkHandler @Inject constructor(
    private val graphs: Set<@JvmSuppressWildcards Graph>
) {

    fun handle(uri: Uri): NavigationHandler.NavigationEvent? {
        val path = uri.path ?: return null
        val screen = routesMap[path] ?: return null
        return NavigationHandler.NavigationEvent.PushScreen(screen)
    }

    // Retrieve all routes registered in the injected graphs
    private val routesMap: Map<String, Screen> by lazy {
        val registeredScreens = mutableMapOf<String, Screen>()

        fun register(screen: Screen) {
            registeredScreens[screen.route] = screen
            if (screen is Graph) {
                screen.children.forEach { clazz ->
                    try {
                        val instance = clazz.getField("INSTANCE").get(null) as? Screen
                        if (instance != null) register(instance)
                    } catch (_: Exception) {
                        // Skip data classes or objects that fail to load
                    }
                }
            }
        }

        graphs.forEach { register(it) }
        registeredScreens
    }
}