package com.dxmxp.navigation.core

import android.net.Uri
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central registry for route lookup and deep link resolution.
 *
 * Responsibilities:
 * - Maintains a typed mapping of routes by [RouteKey]
 * - Resolves incoming deep link URIs to route instances
 * - Supports both static (parameter-less) and dynamic (parameterized) routes
 *
 * The registry is populated at startup from all registered [Graph] instances:
 * - [staticRoutes] from Graph.staticRoutes() → available for fast lookup by key
 * - [dynamicRoutePatterns] from Graph.dynamicRoutePatterns() → pattern matching for URIs
 *
 * Architecture:
 * ```
 * AppGraphsModule
 *     ↓ (provides Set<Graph>)
 * RouteRegistry
 *     ├── routeMap: Map<RouteKey, Route>
 *     │   └── Built from Graph.staticRoutes()
 *     │
 *     ├── pathMap: Map<String, Route>
 *     │   └── String-based lookup for deep links
 *     │
 *     └── routeCreators: Map<String, (Uri) -> Route?>
 *         └── Pattern matchers from Graph.dynamicRoutePatterns()
 *             + additional registered via register()
 * ```
 *
 * Usage:
 * ```
 * // Lookup by typed key (type-safe, recommended for internal navigation)
 * val route: Route? = registry.getRoute(RouteKey.of("/stories"))
 *
 * // Lookup by string path
 * val route: Route? = registry.getRoute("/stories")
 *
 * // Resolve deep link URI
 * val route: Route? = registry.createRouteFromUri(uri)
 * ```
 */
@Singleton
class RouteRegistry @Inject constructor(
    private val graphs: Set<@JvmSuppressWildcards Graph>,
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
     * Useful for feature modules that want to register routes after initialization.
     *
     * @param pattern The route path pattern (e.g., "/stories/detail")
     * @param creator Function to convert URI to Route instance
     *
     * Example:
     * ```
     * registry.register("/custom/path") { uri ->
     *     val param = uri.getQueryParameter("param") ?: return@register null
     *     CustomRoute(param)
     * }
     * ```
     */
    fun register(pattern: String, creator: (Uri) -> Route?) {
        routeCreators[pattern] = creator
    }

    /**
     * Gets a route by its typed key (type-safe lookup).
     * Recommended for internal navigation within the app.
     *
     * @param key The route key
     * @return The registered route, or null if not found
     */
    fun getRoute(key: RouteKey): Route? = routeMap[key]

    /**
     * Resolves a deep link URI to a Route instance.
     *
     * Resolution process:
     * 1. Normalize the URI path (trim trailing slashes)
     * 2. Try exact match against static routes
     * 3. Try pattern matching against dynamic route creators (in order registered)
     * 4. Return null if no match found
     *
     * @param uri The incoming deep link URI
     * @return The resolved Route instance, or null if not resolvable
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
     *
     * @param path The route path (e.g., "/stories")
     * @return The registered route, or null if not found
     */
    fun getRoute(path: String): Route? = pathMap[normalizePath(path)]

    /**
     * Normalizes a URI path for consistent matching.
     * - Removes trailing slashes
     * - Returns null if path is empty/blank
     */
    private fun normalizePath(uri: Uri): String? = uri.path
        ?.trimEnd('/')
        ?.ifBlank { null }

    /**
     * Normalizes a string path for consistent matching.
     * - Removes trailing slashes
     * - Defaults to "/" if empty
     */
    private fun normalizePath(path: String): String = path.trimEnd('/').ifEmpty { "/" }
}

/**
 * A registration entry for a route within a graph.
 * Encapsulates the typed key and the route instance.
 *
 * Used by [Graph.staticRoutes] to declare routes at startup.
 */
data class RouteRegistration(
    val key: RouteKey,
    val route: Route,
)

/**
 * Type-safe wrapper for route paths.
 *
 * Advantages over raw String:
 * - Compile-time safety: impossible to pass arbitrary strings where RouteKey is expected
 * - Validation: route keys cannot be blank
 * - Clarity: strongly signals this is a navigation key, not arbitrary text
 * - IDE support: better refactoring, navigation, and code completion
 *
 * Usage:
 * ```
 * val key = RouteKey.of("/stories")
 * val route = registry.getRoute(key)  // Type-safe
 * ```
 */
@JvmInline
value class RouteKey private constructor(val value: String) {
    init {
        require(value.isNotBlank()) { "Route key cannot be blank" }
    }

    override fun toString(): String = value

    companion object {
        /**
         * Creates a typed route key from a path string.
         * @param value The route path (must be non-blank)
         * @throws IllegalArgumentException if value is blank
         */
        fun of(value: String): RouteKey = RouteKey(value)
    }
}

/**
 * Extension function to convert a Route to its typed key.
 */
fun Route.asKey(): RouteKey = RouteKey.of(route)
