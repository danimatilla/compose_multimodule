package com.dxmxp.seed.screens.rockets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dxmxp.ui.common.InfiniteScrollHandler
import com.dxmxp.ui.common.SeedPullRefresh
import com.dxmxp.ui.model.BeerUiModel
import com.dxmxp.ui.navigation.helpers.NavigationHandler

@Composable
fun BeersScreen(
    onNavigationEvent: (NavigationHandler.NavigationEvent) -> Unit,
    viewModel: BeersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    InfiniteScrollHandler(
        listState = listState,
        isLoading = state.isLoading,
        endReached = state.endReached,
        buffer = 5,
        onLoadNextPage = { viewModel.setEvent(BeersViewModel.Event.LoadNextPage) }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading && state.beers.isNullOrEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        }

        SeedPullRefresh(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.setEvent(BeersViewModel.Event.Refresh) },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.beers.orEmpty(), key = { it.id }) { beer ->
                    BeerItem(
                        beer = beer,
                        onClick = { viewModel.setEvent(BeersViewModel.Event.OnBeerClicked(beer)) }
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BeersViewModel.Effect.NavigateToDetail -> {
                    // Navigation logic here
                }
            }
        }
    }
}

@Composable
private fun BeerItem(
    beer: BeerUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${beer.name} (${beer.volumeText})",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
