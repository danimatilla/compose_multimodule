package com.dxmxp.navigation.core

import android.net.Uri
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route

/**
 * Central registry for route lookup and deep link resolution.
 *
 * The registry depends only on the graph collection; the application layer decides
 * how that collection is provided (Hilt, Koin, manual composition, etc.).
 */
class RouteRegistry(
    private val graphs: Set<Graph>,
) {
    /**
     * Finds the graph that contains the given route.
     * Checks both static routes and path prefixes for dynamic routes.
     */
    fun getGraphForRoute(route: Route): Graph? {
        // 1. Try to find an exact static match
        val staticMatch = graphs.find { graph ->
            graph.staticRoutes().any { it.route == route }
        }
        if (staticMatch != null) return staticMatch

        // 2. Fallback to path prefix matching (for dynamic routes)
        // Sort by length descending to get the most specific match
        return graphs
            .filter { route.route.startsWith(it.route) }
            .maxByOrNull { it.route.length }
    }

    /**
     * Maps [RouteKey] to [Route] for type-safe lookups.
     * Built from all static routes in registered graphs.
     */
    private val routeMap: Map<RouteKey, Route> =
        graphs.flatMap { it.staticRoutes() }.associate { it.key to it.route }

    /**
     * Maps route path strings to [Route] instances.
     * Derived from [routeMap] for deep link path-based lookups.
     */
    private val pathMap: Map<String, Route> =
        routeMap.mapKeys { it.key.value }

    /**
     * Dynamic route pattern matchers for parameterized routes.
     * Populated at construction from all graphs' dynamicRoutePatterns().
     * Can be extended at runtime via register().
     */
    private val routeCreators = mutableMapOf<String, (Uri) -> Route?>()

    init {
        // Register dynamic patterns from all graphs at startup
        graphs.forEach { graph ->
            routeCreators.putAll(graph.dynamicRoutePatterns())
        }
    }

    /**
     * Registers an additional dynamic route pattern at runtime.
     */
    fun register(pattern: String, creator: (Uri) -> Route?) {
        routeCreators[pattern] = creator
    }

    /**
     * Gets a route by its typed key (type-safe lookup).
     */
    fun getRoute(key: RouteKey): Route? = routeMap[key]

    /**
     * Resolves a deep link URI to a Route instance.
     */
    fun createRouteFromUri(uri: Uri): Route? {
        val path = normalizePath(uri) ?: return null
        return getRoute(path)
            ?: run dynamicLookup@{
                for ((pattern, creator) in routeCreators) {
                    if (path.startsWith(pattern)) {
                        val route = creator(uri)
                        if (route != null) return@dynamicLookup route
                    }
                }
                null
            }
    }

    /**
     * Gets a route by its path string (for deep linking).
     */
    fun getRoute(path: String): Route? = pathMap[normalizePath(path)]

    /**
     * Determines if a route is modal.
     */
    fun isModal(route: Route?): Boolean =
        route != null && (getGraphForRoute(route)?.isModal == true || route.isModal)

    /**
     * Determines if the main BottomBar should be shown for a route.
     */
    fun shouldShowBottomBar(route: Route?): Boolean =
        route != null && (getGraphForRoute(route)?.showMainBottomBar != false && route.showMainBottomBar)

    /**
     * Determines if a route requires authentication.
     */
    fun requiresAuth(route: Route?): Boolean =
        route != null && (getGraphForRoute(route)?.requiresAuth != false && route.requiresAuth)

    /**
     * Normalizes a URI path for consistent matching.
     */
    private fun normalizePath(uri: Uri): String? = uri.path
        ?.trimEnd('/')
        ?.ifBlank { null }

    /**
     * Normalizes a string path for consistent matching.
     */
    private fun normalizePath(path: String): String = path.trimEnd('/').ifEmpty { "/" }
}

/**
 * A registration entry for a route within a graph.
 */
data class RouteRegistration(
    val key: RouteKey,
    val route: Route,
)

/**
 * Type-safe wrapper for route paths.
 */
@JvmInline
value class RouteKey private constructor(val value: String) {
    init {
        require(value.isNotBlank()) { "RouteKey cannot be blank" }
    }

    companion object {
        fun of(value: String): RouteKey = RouteKey(value)
    }
}
