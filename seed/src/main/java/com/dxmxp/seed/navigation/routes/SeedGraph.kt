package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.dash.HomeScreen
import com.dxmxp.seed.screens.dash.MenuScreen
import com.dxmxp.seed.screens.dash.SearchScreen
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.NavigationHandler
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.navigation.screenEntry
import com.dxmxp.ui.screens.WebView
import com.dxmxp.ui.screens.WebViewScreen
import kotlinx.serialization.Serializable

@Serializable
object SeedGraph : Graph {

    @Serializable
    data object Search : Screen {
        override val route: String
            get() = "${this@SeedGraph.route}/search"
    }

    @Serializable
    data object Menu : Screen {
        override val route: String
            get() = "${this@SeedGraph.route}/menu"
    }

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        screenEntry<SeedGraph> { HomeScreen(onEvent) }
        screenEntry<Search> { SearchScreen(onEvent) }
        screenEntry<Menu> { MenuScreen(onEvent) }
        screenEntry<WebView> { screen -> WebViewScreen(screen, onEvent) }

        // Integrate the ProfileGraph entries into this graph.
        ProfileGraph.run { registerEntries(onEvent) }

        // Integrate the StoriesGraph module into this graph.
        StoriesGraph.run { registerEntries(onEvent) }
    }

    override val route: String
        get() = "/seed"
}
