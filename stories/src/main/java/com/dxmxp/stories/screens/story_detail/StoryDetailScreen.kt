package com.dxmxp.stories.screens.story_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dxmxp.ui.navigation.helpers.NavigationHandler


@Composable
fun StoryDetailScreen(
    viewModel: StoryDetailViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Story Detail Screen")
        Text("Story ID: ${state.storyId}")
        state.story?.let { story ->
            Text("Title: ${story.title}")
            Text("Description: ${story.description}")
        }
    }
}
