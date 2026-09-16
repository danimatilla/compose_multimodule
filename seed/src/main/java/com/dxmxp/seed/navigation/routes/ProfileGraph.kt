package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.core.RouteKey
import com.dxmxp.navigation.core.RouteRegistration
import com.dxmxp.seed.screens.profile.ProfileScreen
import com.dxmxp.seed.screens.profile.SettingsScreen
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import kotlinx.serialization.Serializable

/**
 * ProfileGraph represents the navigation context for user profile and account settings.
 *
 * Route: /profile
 * Screens:
 * - Profile: /profile (user profile view)
 * - Settings: /profile/settings (account settings)
 *
 * This graph is typically accessible from the main app and can also be
 * navigated to directly from deep links or cross-module navigation.
 *
 * Type-safe navigation:
 * ```
 * navigator.push(ProfileGraph)
 * navigator.push(ProfileGraph.Settings)
 * ```
 *
 * Deep link resolution:
 * - app://profile → ProfileGraph
 * - app://profile/settings → ProfileGraph.Settings
 */
@Serializable
data object ProfileGraph : Graph {

    override val route: String get() = "/profile"

    @Serializable
    data object Settings : Screen {
        override val route: String get() = "${ProfileGraph.route}/settings"
    }

    /**
     * Declare this graph and its static screens in the route registry.
     */
    override fun staticRoutes(): Set<RouteRegistration> = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(Settings.route), Settings),
    )

    /**
     * Register all composable screens for this graph.
     */
    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<ProfileGraph> { ProfileScreen() }
        screenEntry<Settings> { SettingsScreen() }
    }
}
