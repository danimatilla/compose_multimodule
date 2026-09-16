package com.dxmxp.navigation.model

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.core.RouteKey
import com.dxmxp.navigation.core.RouteRegistration
import android.net.Uri
import com.dxmxp.navigation.model.Route

/**
 * A Graph is a collection of related screens that share a common navigation context.
 *
 * Each graph defines:
 * - A route path (e.g., "/stories", "/profile")
 * - Whether it requires authentication and shows the bottom bar
 * - A set of static routes (screens without parameters)
 * - Dynamic route patterns for deep linking (screens with parameters)
 * - Screen registration for the navigation system
 *
 * Example:
 * ```
 * @Serializable
 * data object MainGraph : Graph {
 *     override val route: String = "/main"
 *
 *     @Serializable
 *     data object Home : Screen {
 *         override val route: String = "/main/home"
 *     }
 *
 *     override fun staticRoutes() = setOf(
 *         RouteRegistration(RouteKey.of(route), this),
 *         RouteRegistration(RouteKey.of(Home.route), Home),
 *     )
 *
 *     override fun dynamicRoutePatterns() = mapOf(
 *         "/main/detail" to { uri ->
 *             val id = uri.getQueryParameter("id") ?: return@mapOf null
 *             DetailScreen(id)
 *         }
 *     )
 * }
 * ```
 */
interface Graph : Route {
    override val showMainBottomBar: Boolean get() = true

    /** Whether this graph should be presented as a modal dialog */
    val isModal: Boolean get() = false

    /**
     * Register all screens of this graph within the navigation system.
     * This is called by the router to set up the composable hierarchy.
     */
    fun EntryProviderScope<NavKey>.registerScreens()

    /**
     * Define static routes (screens without parameters) for this graph.
     * These routes are registered at startup and accessible via [RouteRegistry.getRoute(RouteKey)].
     *
     * Static routes must be either `data object` or singleton instances.
     * Use [dynamicRoutePatterns] for routes with parameters.
     *
     * @return Set of static route registrations
     */
    fun staticRoutes(): Set<RouteRegistration> = setOf(
        RouteRegistration(RouteKey.of(route), this)
    )

    /**
     * Define dynamic route patterns for deep linking.
     * Useful for parameterized routes like `/stories/detail?id=123`.
     *
     * The URI parser will attempt to match the incoming deep link path against
     * each pattern in order. The first match will be used.
     *
     * @return Map of path patterns to URI -> Route converter functions
     *
     * Example:
     * ```
     * override fun dynamicRoutePatterns() = mapOf(
     *     "/stories/detail" to { uri ->
     *         val id = uri.getQueryParameter("id") ?: return@mapOf null
     *         StoryDetail(id = id)
     *     }
     * )
     * ```
     */
    fun dynamicRoutePatterns(): Map<String, (Uri) -> Route?> = emptyMap()
}
