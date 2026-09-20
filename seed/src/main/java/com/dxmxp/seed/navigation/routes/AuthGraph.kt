package com.dxmxp.seed.navigation.routes

import android.net.Uri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.core.RouteKey
import com.dxmxp.navigation.core.RouteRegistration
import com.dxmxp.seed.screens.login.LoginScreen
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import kotlinx.serialization.Serializable

/**
 * AuthGraph represents the navigation context for unauthenticated users.
 *
 * Route: /auth
 * Screens:
 * - Login: /auth/login (login interface)
 *
 * This graph is typically shown before the user acquires a valid session token.
 * After successful authentication, navigation switches to MainGraph.
 *
 * Override: requiresAuth = false (publicly accessible, does not require token)
 *
 * Type-safe navigation:
 * ```
 * navigator.setRoot(AuthGraph)
 * navigator.push(AuthGraph.Login)
 * ```
 *
 * Deep link resolution:
 * - app://auth → AuthGraph
 * - app://auth/login → AuthGraph.Login
 */
@Serializable
data object AuthGraph : Graph {

    override val route: String get() = "/auth"
    override val requiresAuth: Boolean get() = false
    override val showMainBottomBar: Boolean get() = false

    @Serializable
    data object Login : Screen {
        override val route: String get() = "${AuthGraph.route}/login"
    }

    /**
     * Declare this graph and its static screens in the route registry.
     */
    override fun staticRoutes(): Set<RouteRegistration> = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(Login.route), Login),
    )

    override fun dynamicRoutePatterns(): Map<String, (Uri) -> Route?> = emptyMap()

    /**
     * Register all composable screens for this graph.
     */
    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<AuthGraph> { LoginScreen() }
        screenEntry<Login> { LoginScreen() }
    }
}
