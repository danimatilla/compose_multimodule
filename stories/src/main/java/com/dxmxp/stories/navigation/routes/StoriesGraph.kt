package com.dxmxp.stories.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.stories.screens.HomeScreen
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.NavigationHandler
import com.dxmxp.ui.navigation.Screen
import kotlinx.serialization.Serializable

@Serializable
object StoriesGraph : Graph {

    @Serializable
    data object Search : Screen {
        override val route: String
            get() = "${this@StoriesGraph.route}/search"
    }

    @Serializable
    data object Notifications : Screen {
        override val route: String
            get() = "${this@StoriesGraph.route}/notifications"
    }

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        entry<StoriesGraph> { HomeScreen(onEvent) }
        entry<Search> { /* SearchScreen(onEvent) */ }
        entry<Notifications> { /* NotificationsScreen(onEvent) */ }
    }

    override val route: String
        get() = "/stories"
}