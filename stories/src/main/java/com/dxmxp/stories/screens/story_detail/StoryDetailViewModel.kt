package com.dxmxp.stories.screens.story_detail

import com.dxmxp.domain.model.Story
import com.dxmxp.ui.base.DataObserver
import com.dxmxp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel @Inject constructor(
    private val dataObserver: DataObserver
) : BaseViewModel<StoryDetailViewModel.State, StoryDetailViewModel.Effect, StoryDetailViewModel.Event>() {

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> {
                val story = dataObserver.getLast<Story>()
                setState { copy(storyId = event.id, story = story) }
            }
        }
    }

    sealed interface Event {
        data class Init(val id: String) : Event
    }

    data class State(
        val storyId: String? = null,
        val story: Story? = null
    )

    interface Effect

}
