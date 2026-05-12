package com.example.seed.navigation.screens

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.seed.navigation.NavigationHandler
import com.example.seed.screens.dash.HomeScreen
import com.example.seed.screens.dash.MenuScreen
import com.example.seed.screens.dash.SearchScreen
import com.example.seed.screens.dash.profile.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
object DashGraph : Graph {

    @Serializable
    data object Menu : Screen {
        override val route: String
            get() = "${this@DashGraph.route}/menu"
    }

    @Serializable
    data object Home : Screen {
        override val route: String
            get() = "${this@DashGraph.route}/home"
    }

    @Serializable
    data object Search : Screen {
        override val route: String
            get() = "${this@DashGraph.route}/search"
    }

    @Serializable
    data object Stories : Screen {
        override val route: String
            get() = "${this@DashGraph.route}/stories"
    }

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        entry<Home> { HomeScreen(onEvent) }
        entry<Search> { SearchScreen(onEvent) }
        entry<Menu> { MenuScreen(onEvent) }

        // Integrate the StoriesGraph module into this graph.
        // StoriesGraph.run { registerEntries(onEvent) }
        entry<Stories> { /* StoriesScreen(onEvent) */ }

        // Integrate the ProfileGraph entries into this graph.
        ProfileGraph.run { registerEntries(onEvent) }
        entry<ProfileGraph> { ProfileScreen(onEvent) }
    }

    override val route: String
        get() = "/seed"
}