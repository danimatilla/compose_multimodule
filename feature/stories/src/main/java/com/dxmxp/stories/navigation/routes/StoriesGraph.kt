package com.dxmxp.stories.navigation.routes

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.dxmxp.domain.model.Story
import com.dxmxp.stories.navigation.StoriesScaffold
import com.dxmxp.stories.screens.feed.FeedScreen
import com.dxmxp.navigation.utils.NavigationUtils.modalAnimation
import com.dxmxp.stories.screens.story_detail.StoryDetailScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailViewModel
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import kotlinx.serialization.Serializable

@Serializable
data object StoriesGraph : Graph {

    override val isModal: Boolean get() = true
    override val route: String get() = "/stories"

    @Serializable
    data object Feed : Screen {
        override val route: String get() = "${StoriesGraph.route}/home"
    }

    @Serializable
    data object Profile : Screen {
        override val route: String get() = "${StoriesGraph.route}/profile"
    }

    @Serializable
    data class StoryDetail(val id: String, val story: Story? = null) : Screen {
        override val route: String get() = "${StoriesGraph.route}/detail"
    }

    @Serializable
    data object Notifications : Screen {
        override val route: String get() = "${StoriesGraph.route}/notifications"
    }

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<StoriesGraph>(
            metadata = metadata { modalAnimation() }
        ) { StoriesScaffold() }
        screenEntry<Feed> { FeedScreen() }
        screenEntry<StoryDetail, StoryDetailViewModel>(
            viewModelProvide = { hiltViewModel<StoryDetailViewModel>() },
        ) { viewModel -> StoryDetailScreen(viewModel) }
        screenEntry<Profile> {}
        screenEntry<Notifications> {}
    }
}

fun EntryProviderScope<NavKey>.storiesGraph() {
    with(StoriesGraph) { registerScreens() }
}
