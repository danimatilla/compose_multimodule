package com.dxmxp.stories.screens.story_detail

import com.dxmxp.domain.model.StoryBo
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.base.DataObserver
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.base.InitializableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel @Inject constructor(
    private val dataObserver: DataObserver,
) : BaseViewModel<StoryDetailViewModel.State, StoryDetailViewModel.Effect, StoryDetailViewModel.Event>(),
    InitializableViewModel<StoriesScaffoldGraph.StoryDetail> {

    override fun createInitialState(): State = State()

    override fun init(screen: StoriesScaffoldGraph.StoryDetail) {
        val story = dataObserver.getLast<StoryBo>()
        setState { copy(storyId = screen.id, story = story) }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> {
                init(StoriesScaffoldGraph.StoryDetail(event.id))
            }
        }
    }

    sealed interface Event {
        data class Init(val id: String) : Event
    }

    data class State(
        val storyId: String? = null,
        val story: StoryBo? = null
    )

    interface Effect

}
