package com.dxmxp.stories.screens.feed

import com.dxmxp.domain.model.StoryBo
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
        data class OpenDetail(val story: StoryBo) : Event
    }

    data class State(
        val stories: List<StoryBo>? = null
    )

    interface Effect {
        data class OpenDetail(val story: StoryBo) : Effect
    }

    init {
        fetchStories()
    }

    private fun fetchStories() {
        val stories = (1..20).map {
            StoryBo(
                id = "$it",
                title = "Story - $it",
                description = "Description story - $it"
            )
        }
        setState { copy(stories = stories) }
    }

    private fun openDetail(story: StoryBo) {
        setEffect { Effect.OpenDetail(story) }
    }
}