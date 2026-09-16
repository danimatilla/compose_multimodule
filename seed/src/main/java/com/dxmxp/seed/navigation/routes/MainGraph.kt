package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.core.RouteKey
import com.dxmxp.navigation.core.RouteRegistration
import com.dxmxp.seed.navigation.MainScaffold
import com.dxmxp.seed.screens.HomeScreen
import com.dxmxp.seed.screens.MenuScreen
import com.dxmxp.seed.screens.SearchScreen
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import com.dxmxp.stories.navigation.routes.StoriesGraph
import kotlinx.serialization.Serializable

/**
 * MainGraph represents the primary navigation context after user authentication.
 *
 * Route: /seed
 * Screens:
 * - Home: /seed/home (home feed)
 * - Search: /seed/search (search interface)
 * - Menu: /seed/menu (main menu)
 *
 * Also composes the StoriesGraph and ProfileGraph as nested navigation contexts.
 *
 * Type-safe navigation:
 * ```
 * navigator.push(MainGraph)
 * navigator.push(MainGraph.Home)
 * navigator.push(MainGraph.Search)
 * ```
 *
 * Deep link resolution:
 * - app://seed → MainGraph
 * - app://seed/home → MainGraph.Home
 * - app://seed/search → MainGraph.Search
 * - app://seed/menu → MainGraph.Menu
 */
@Serializable
data object MainGraph : Graph {

    override val route: String get() = "/seed"

    @Serializable
    data object Home : Screen {
        override val route: String get() = "${MainGraph.route}/home"
    }

    @Serializable
    data object Search : Screen {
        override val route: String get() = "${MainGraph.route}/search"
    }

    @Serializable
    data object Menu : Screen {
        override val route: String get() = "${MainGraph.route}/menu"
    }

    /**
     * Declare this graph and all its static screens in the route registry.
     * These are accessible via type-safe keys or deep links.
     */
    override fun staticRoutes(): Set<RouteRegistration> = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(Home.route), Home),
        RouteRegistration(RouteKey.of(Search.route), Search),
        RouteRegistration(RouteKey.of(Menu.route), Menu),
    )

    /**
     * Register all composable screens for this graph.
     * Called by the navigation engine to set up the UI hierarchy.
     */
    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<MainGraph> { MainScaffold() }
        screenEntry<Home> { HomeScreen() }
        screenEntry<Search> { SearchScreen() }
        screenEntry<Menu> { MenuScreen() }
        // Compose nested graphs
        StoriesGraph.run { registerScreens() }
        ProfileGraph.run { registerScreens() }
    }
}
