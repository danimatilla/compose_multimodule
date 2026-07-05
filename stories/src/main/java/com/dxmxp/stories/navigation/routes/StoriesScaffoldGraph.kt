package com.dxmxp.stories.navigation.routes

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.domain.model.Story
import com.dxmxp.stories.screens.feed.FeedScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailViewModel
import com.dxmxp.ui.navigation.model.Graph
import com.dxmxp.ui.navigation.model.Screen
import com.dxmxp.ui.navigation.model.Screen.Companion.screenEntry
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

    override val screens: List<Class<out Screen>>
        get() = listOf(
            Feed::class.java,
            StoryDetail::class.java,
            Profile::class.java,
            Notifications::class.java
        )

    @Serializable
    data object Feed : Screen {
        override val route: String
            get() = "${this@StoriesScaffoldGraph.route}/feed"
    }

    @Serializable
    data class StoryDetail(val id: String, val story: Story? = null) : Screen

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
    fun provideStoriesScaffoldGraph(): Graph = StoriesScaffoldGraph
}
