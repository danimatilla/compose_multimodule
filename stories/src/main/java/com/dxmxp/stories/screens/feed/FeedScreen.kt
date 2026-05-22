package com.dxmxp.stories.screens.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.domain.model.Story
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.ui.screens.BottomBar
import com.dxmxp.ui.screens.SeedScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onEvent: (NavigationHandler.NavigationEvent) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val navigationBarItems = remember {
        listOf(
            StoriesGraph to Icons.Default.Home,
            StoriesGraph.Notifications to Icons.Default.Notifications,
            StoriesGraph.Profile to Icons.Default.Person,
        )
    }
    val backStack = rememberNavBackStack()

    SeedScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        content = { Icon(Icons.Default.Close, contentDescription = "Close") },
                        onClick = { onEvent(NavigationHandler.NavigationEvent.PopScreen()) }
                    )
                }
            )
        },
        bottomBar = {
                BottomBar(
                    navigationBarItems = navigationBarItems,
                    shouldShowBottomBar = true,
                    backStack = backStack,
                    onClickItem = { screen ->
                        onEvent(NavigationHandler.NavigationEvent.PushScreen(screen))
                    }
                )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Content(
                state = state,
                onEvent = viewModel::setEvent
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FeedViewModel.Effect.OpenDetail -> {
                    val story = effect.story
                    onEvent(
                        NavigationHandler.NavigationEvent.PushScreen(
                            screen = StoriesGraph.StoryDetail(story.id),
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
        LazyColumn(

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
    } ?: Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Feed Screen")
    }
}

@Preview
@Composable
private fun FeedScreenPreview() {
    Content(
        state = FeedViewModel.State(
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
