package com.dxmxp.stories.screens.feed

import com.dxmxp.domain.model.Story
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.navigation.core.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : BaseViewModel<FeedViewModel.State, FeedViewModel.Effect, FeedViewModel.Event>() {

    data class State(
        val stories: List<Story>? = null
    )

    interface Event {
        data class OpenDetail(val story: Story) : Event
    }

    interface Effect {
        data class OpenDetail(val story: Story) : Effect
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OpenDetail -> openDetail(event.story)
        }
    }

    init {
        fetchStories()
    }

    private fun fetchStories() {
        val stories = (1..20).map {
            Story(
                id = "$it",
                title = "Story - $it",
                description = "Description story - $it"
            )
        }
        setState { copy(stories = stories) }
    }

    private fun openDetail(story: Story) {
        navigationManager.push(
            route = StoriesScaffoldGraph.StoryDetail(id = story.id, story = story)
        )
    }
}
