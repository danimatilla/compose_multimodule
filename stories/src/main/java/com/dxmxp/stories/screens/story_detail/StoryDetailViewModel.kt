package com.dxmxp.stories.screens.story_detail

import com.dxmxp.domain.model.Story
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.common.InitializableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel @Inject constructor() :
    BaseViewModel<StoryDetailViewModel.State, StoryDetailViewModel.Effect, StoryDetailViewModel.Event>(),
    InitializableViewModel<StoriesScaffoldGraph.StoryDetail> {

    data class State(
        val storyId: String? = null,
        val story: Story? = null
    )

    interface Event {
        data class Init(val id: String) : Event
    }

    interface Effect

    override fun createInitialState(): State = State()

    override fun init(screen: StoriesScaffoldGraph.StoryDetail) {
        setState { copy(storyId = screen.id, story = screen.story) }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> {
                init(StoriesScaffoldGraph.StoryDetail(event.id))
            }
        }
    }
}
