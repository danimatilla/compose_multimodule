package com.dxmxp.stories.screens.feed

import com.dxmxp.domain.model.Story
import com.dxmxp.navigation.core.NavAction
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel<HomeViewModel.State, HomeViewModel.Effect, HomeViewModel.Event>() {

    data class State(
        val stories: List<Story>? = null
    )

    interface Event {
        data class OpenDetail(val story: Story) : Event
    }

    interface Effect {
        data class Navigate(val action: NavAction) : Effect
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OpenDetail -> {
                val route = StoriesGraph.StoryDetail(id = event.story.id, story = event.story)
                setEffect { Effect.Navigate(NavAction.Push(route)) }
            }
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
}
