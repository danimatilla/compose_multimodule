package com.dxmxp.stories.navigation.routes

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.stories.screens.feed.FeedScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailViewModel
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.navigation.Screen.Companion.screenEntry
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import kotlinx.serialization.Serializable

@Serializable
object StoriesScaffoldGraph : Graph {

    override val isModal: Boolean
        get() = true

    @Serializable
    data class StoryDetail(val id: String) : Screen {
        override val route: String
            get() = "${this@StoriesScaffoldGraph.route}/story"
    }

    @Serializable
    data object Profile : Screen {
        override val route: String
            get() = "${this@StoriesScaffoldGraph.route}/profile"
    }

    @Serializable
    data object Notifications : Screen {
        override val route: String
            get() = "${this@StoriesScaffoldGraph.route}/notifications"
    }

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit,
    ) {
        screenEntry<StoriesScaffoldGraph> { FeedScreen(onEvent) }
        screenEntry<StoryDetail, StoryDetailViewModel>(
            viewModelProvide = { hiltViewModel<StoryDetailViewModel>() },
        ) { viewModel -> StoryDetailScreen(onEvent, viewModel) }
        screenEntry<Profile> { /* ProfileScreen(onEvent) */ }
        screenEntry<Notifications> { /* NotificationsScreen(onEvent) */ }
    }

    override val route: String
        get() = "/stories"
}
