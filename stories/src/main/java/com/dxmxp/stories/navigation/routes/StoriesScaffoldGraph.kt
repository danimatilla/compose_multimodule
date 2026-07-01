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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
@Module
@InstallIn(SingletonComponent::class)
object StoriesScaffoldGraph : Graph {

    override val isModal: Boolean
        get() = true

    @Serializable
    data object Feed : Screen {
        override val route: String
            get() = "${this@StoriesScaffoldGraph.route}/feed"
    }

    @Serializable
    data class StoryDetail(val id: String) : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Notifications : Screen

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<Feed> { FeedScreen() }
        screenEntry<StoryDetail, StoryDetailViewModel>(
            viewModelProvide = { hiltViewModel<StoryDetailViewModel>() },
        ) { viewModel -> StoryDetailScreen(viewModel) }
        screenEntry<Profile> { /* ProfileScreen() */ }
        screenEntry<Notifications> { /* NotificationsScreen() */ }
    }

    override val route: String
        get() = "/stories"

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = StoriesScaffoldGraph
}
