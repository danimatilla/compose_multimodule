package com.dxmxp.stories.screens.story_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.components.SeedTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryDetailScreen(
    viewModel: StoryDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current

    Content(
        state = state,
        onEvent = viewModel::setEvent
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            // TODO: Add effects here.
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: StoryDetailViewModel.State,
    onEvent: (StoryDetailViewModel.Event) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SeedTopAppBar(
            title = { Text("Story Detail") },
            currentRoute = state.storyId?.let { StoriesGraph.StoryDetail(it) }
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Story Detail Screen")
            Text("Story ID: ${state.storyId}")
        }
    }
}
