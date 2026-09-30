package com.dxmxp.navigation.utils

import android.net.Uri
import com.dxmxp.navigation.core.NavAction
import com.dxmxp.navigation.core.RouteRegistry
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of processing a deep link.
 */
sealed interface DeepLinkResult {
    data object Unresolved : DeepLinkResult
    data class RequiresAuth(val pendingRoute: Route, val action: NavAction?) : DeepLinkResult
    data class Success(val targetRoute: Route, val action: NavAction) : DeepLinkResult
}

/**
 * Centralized Deep Link router in the navigation library.
 * Resolves URIs, enforces authentication requirements, and generates
 * the exact [NavAction] hierarchy.
 */
@Singleton
class DeepLinkRouter @Inject constructor(
    private val routeRegistry: RouteRegistry,
) {
    /**
     * Resolves a URI to a [Route].
     */
    fun resolve(uri: Uri): Route? = routeRegistry.createRouteFromUri(uri)

    /**
     * Builds the exact navigation action hierarchy for a target route within a main graph.
     */
    fun process(route: Route, mainGraph: Graph): NavAction {
        val graph = routeRegistry.getGraphForRoute(route)
        val isModal = routeRegistry.isModal(route)

        return NavAction.UpdateStack {
            if ((graph == null) || (graph == mainGraph)) {
                if (isModal) push(route)
                else root(route)
            } else {
                val configuredGraph = if (route != graph) {
                    graph.withInitialRoute(route)
                } else {
                    graph
                }

                root(mainGraph)
                if (isModal) {
                    push(configuredGraph)
                } else {
                    root(configuredGraph)
                }
            }
        }
    }

    /**
     * Processes a deep link and builds the exact [NavAction] UpdateStack hierarchy.
     */
    fun handle(
        uri: Uri,
        mainGraph: Graph,
        authGraph: Graph? = null,
        isAuthenticated: Boolean = true,
    ): DeepLinkResult {
        val targetRoute = resolve(uri) ?: return DeepLinkResult.Unresolved

        // 1. Authentication check
        if (routeRegistry.requiresAuth(targetRoute) && !isAuthenticated) {
            val authAction = authGraph?.let { NavAction.Root(it) }
            return DeepLinkResult.RequiresAuth(
                pendingRoute = targetRoute,
                action = authAction,
            )
        }

        val action = process(targetRoute, mainGraph)
        return DeepLinkResult.Success(targetRoute = targetRoute, action = action)
    }
}
