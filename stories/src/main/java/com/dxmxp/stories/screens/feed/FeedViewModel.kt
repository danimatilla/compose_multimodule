package com.dxmxp.stories.screens.feed

import com.dxmxp.domain.model.Story
import com.dxmxp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor() :
    BaseViewModel<FeedViewModel.State, FeedViewModel.Effect, FeedViewModel.Event>() {

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OpenDetail -> openDetail(event.story)
        }
    }

    interface Event {
        data class OpenDetail(val story: Story) : Event
    }

    data class State(
        val stories: List<Story>? = null
    )

    interface Effect {
        data class OpenDetail(val story: Story) : Effect
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
        setEffect { Effect.OpenDetail(story) }
    }
}