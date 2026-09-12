package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.navigation.MainScaffold
import com.dxmxp.seed.screens.HomeScreen
import com.dxmxp.seed.screens.MenuScreen
import com.dxmxp.seed.screens.SearchScreen
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import com.dxmxp.stories.navigation.routes.storiesGraph
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

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<MainGraph> { MainScaffold() }
        screenEntry<Home> { HomeScreen() }
        screenEntry<Search> { SearchScreen() }
        screenEntry<Menu> { MenuScreen() }
        storiesGraph()
        profileGraph()
    }
}

fun EntryProviderScope<NavKey>.mainGraph() {
    with(MainGraph) { registerScreens() }
}
