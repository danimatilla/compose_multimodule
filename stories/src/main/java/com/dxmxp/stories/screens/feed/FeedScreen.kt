package com.dxmxp.stories.screens.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dxmxp.domain.model.StoryBo
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.navigation.helpers.NavigationHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onEvent: (NavigationHandler.NavigationEvent) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Content(
        state = state,
        onEvent = viewModel::setEvent
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FeedViewModel.Effect.OpenDetail -> {
                    val story = effect.story
                    onEvent(
                        NavigationHandler.NavigationEvent.PushScreen(
                            screen = StoriesScaffoldGraph.StoryDetail(story.id),
                            data = story
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    state: FeedViewModel.State,
    onEvent: (FeedViewModel.Event) -> Unit
) {
    state.stories?.let { stories ->
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState
        ) {
            itemsIndexed(stories, key = { _, story -> story.id }) { index, story ->
                ListItem(
                    headlineContent = { Text(story.title) },
                    supportingContent = { Text(story.description) },
                    modifier = Modifier.clickable(
                        onClick = { onEvent(FeedViewModel.Event.OpenDetail(story)) }
                    )
                )

                if (index != stories.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview
@Composable
private fun FeedScreenPreview() {
    Content(
        state = FeedViewModel.State(
            stories = (1..20).map {
                StoryBo(
                    id = "$it",
                    title = "Story - $it",
                    description = "Description story - $it"
                )
            }
        ),
        onEvent = {}
    )
}
