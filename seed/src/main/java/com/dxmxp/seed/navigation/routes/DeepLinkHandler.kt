package com.dxmxp.seed.navigation.routes

import android.net.Uri
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.NavigationHandler.NavigationEvent
import com.dxmxp.ui.navigation.Screen

object DeepLinkHandler {

    fun handleDeepLink(uri: Uri): NavigationEvent? {
        val path = uri.path ?: return null
        val screen = routesMap[path] ?: return null
        return NavigationEvent.PushScreen(screen)
    }

    // Register graphs here
    private val graphs = listOf(
        StoriesGraph,
        ProfileGraph,
        SeedGraph
    )

    // Retrieve all routes registered in the graphs
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
