package com.dxmxp.stories.screens.story_detail

import com.dxmxp.domain.model.Story
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.navigation.common.InitializableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel @Inject constructor() :
    InitializableViewModel<StoriesGraph.StoryDetail>,
    BaseViewModel<StoryDetailViewModel.State, StoryDetailViewModel.Effect, StoryDetailViewModel.Event>()
{
    data class State(
        val storyId: String? = null,
        val story: Story? = null
    )

    interface Event {
        data class Init(val id: String) : Event
    }

    interface Effect

    override fun createInitialState(): State = State()

    override fun init(route: StoriesGraph.StoryDetail) {
        setState { copy(storyId = route.id, story = route.story) }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> init(StoriesGraph.StoryDetail(event.id))
        }
    }
}
