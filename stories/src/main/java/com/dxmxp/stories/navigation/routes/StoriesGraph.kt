package com.dxmxp.stories.navigation.routes

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.stories.screens.feed.FeedScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailViewModel
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.NavigationHandler
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.navigation.screenEntry
import kotlinx.serialization.Serializable

@Serializable
object StoriesGraph : Graph {

    @Serializable
    data class StoryDetail(val id: String) : Screen {
        override val route: String
            get() = "${this@StoriesGraph.route}/story"
    }

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
        onEvent: (NavigationHandler.NavigationEvent) -> Unit,
    ) {
        screenEntry<StoriesGraph> { FeedScreen(onEvent) }
        screenEntry<StoryDetail, StoryDetailViewModel>(
            viewModelProvide = { hiltViewModel<StoryDetailViewModel>() },
        ) { viewModel -> StoryDetailScreen(onEvent, viewModel) }
        screenEntry<Search> { /* SearchScreen(onEvent) */ }
        screenEntry<Notifications> { /* NotificationsScreen(onEvent) */ }
    }

    override val route: String
        get() = "/stories"
}