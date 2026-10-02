package com.dxmxp.stories.screens.home

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dxmxp.domain.model.Story
import com.dxmxp.navigation.core.LocalNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StoriesHomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current

    Content(
        state = state,
        onEvent = viewModel::setEvent
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is StoriesHomeViewModel.Effect.Navigate -> navigator.navAction(effect.action)
            }
        }
    }
}

@Composable
private fun Content(
    state: StoriesHomeViewModel.State,
    onEvent: (StoriesHomeViewModel.Event) -> Unit
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
                        onClick = { onEvent(StoriesHomeViewModel.Event.OpenDetail(story)) }
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
        state = StoriesHomeViewModel.State(
            stories = (1..20).map {
                Story(
                    id = "$it",
                    title = "Story - $it",
                    description = "Description story - $it"
                )
            }
        ),
        onEvent = {}
    )
}
