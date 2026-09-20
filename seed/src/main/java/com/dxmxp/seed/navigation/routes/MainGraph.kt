package com.dxmxp.seed.navigation.routes

import android.net.Uri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.core.RouteKey
import com.dxmxp.navigation.core.RouteRegistration
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import com.dxmxp.seed.screens.HomeScreen
import com.dxmxp.seed.screens.MenuScreen
import com.dxmxp.seed.screens.SearchScreen
import com.dxmxp.stories.navigation.routes.StoriesGraph
import kotlinx.serialization.Serializable


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

    override fun dynamicRoutePatterns(): Map<String, (Uri) -> Route?> = emptyMap()

    /**
     * Register all composable screens for this graph.
     */
    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<MainGraph> { HomeScreen() }
        screenEntry<Home> { HomeScreen() }
        screenEntry<Search> { SearchScreen() }
        screenEntry<Menu> { MenuScreen() }

        // Nested graphs
        StoriesGraph.run { registerScreens() }
        ProfileGraph.run { registerScreens() }
    }
}
