package com.dxmxp.stories.navigation.routes

import android.net.Uri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.core.RouteKey
import com.dxmxp.navigation.core.RouteRegistration
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import com.dxmxp.navigation.utils.routePattern
import com.dxmxp.stories.navigation.StoriesScaffold
import com.dxmxp.stories.screens.home.HomeScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailViewModel
import kotlinx.serialization.Serializable


@Route.ModalRoute
@Serializable
data class StoriesGraph(
    override val initialRoute: Route? = null,
) : Graph {

    override val route: String get() = STORIES_GRAPH_PATH

    override fun withInitialRoute(route: Route): Graph = copy(initialRoute = route)

    @Serializable
    data object Home : Screen {
        override val route: String get() = STORIES_HOME_PATH
    }

    @Serializable
    data object Profile : Screen {
        override val route: String get() = STORIES_PROFILE_PATH
    }

    @Serializable
    data class StoryDetail(val id: String) : Screen {
        override val route: String get() = STORIES_DETAIL_PATH
    }

    override fun staticRoutes(): Set<RouteRegistration> = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(Home.route), Home),
        RouteRegistration(RouteKey.of(Profile.route), Profile),
    )

    override fun dynamicRoutePatterns(): Map<String, (Uri) -> Route?> = mapOf(
        routePattern<StoryDetail>(STORIES_DETAIL_PATH),
    )

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<StoriesGraph> { graph -> StoriesScaffold(initialRoute = graph.initialRoute) }
        screenEntry<Home> { HomeScreen() }
        screenEntry<Profile> { /* ProfileScreen() */ }
        screenEntry<StoryDetail, StoryDetailViewModel>(
            viewModelProvide = { hiltViewModel<StoryDetailViewModel>() },
        ) { viewModel -> StoryDetailScreen(viewModel) }
    }

    private companion object {
        const val STORIES_GRAPH_PATH = "/stories"
        const val STORIES_HOME_PATH = "$STORIES_GRAPH_PATH/home"
        const val STORIES_PROFILE_PATH = "$STORIES_GRAPH_PATH/profile"
        const val STORIES_DETAIL_PATH = "$STORIES_GRAPH_PATH/detail"
    }
}
