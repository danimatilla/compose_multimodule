package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.HomeScreen
import com.dxmxp.seed.screens.MenuScreen
import com.dxmxp.seed.screens.SearchScreen
import com.dxmxp.seed.screens.profile.ProfileScreen
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.navigation.Screen.Companion.screenEntry
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import kotlinx.serialization.Serializable

@Serializable
object MainScaffoldGraph : Graph {

    @Serializable
    data object Search : Screen {
        override val route: String
            get() = "${this@MainScaffoldGraph.route}/search"
    }

    @Serializable
    data object Menu : Screen {
        override val route: String
            get() = "${this@MainScaffoldGraph.route}/menu"
    }

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        screenEntry<MainScaffoldGraph> { HomeScreen(onEvent) }
        screenEntry<Search> { SearchScreen(onEvent) }
        screenEntry<Menu> { MenuScreen(onEvent) }

        // Integrate the ProfileGraph entries into this graph.
        ProfileGraph.run { registerEntries(onEvent) }
    }

    override val route: String
        get() = "/seed"
}
