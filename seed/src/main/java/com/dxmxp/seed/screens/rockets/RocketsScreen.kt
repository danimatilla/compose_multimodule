package com.dxmxp.seed.screens.rockets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dxmxp.domain.model.Rocket
import com.dxmxp.ui.navigation.helpers.NavigationHandler

@Composable
fun RocketsScreen(
    onEvent: (NavigationHandler.NavigationEvent) -> Unit,
    viewModel: RocketsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RocketsViewModel.Effect.NavigateToDetail -> {
                    // Here you would convert the VM Effect to a NavigationEvent
                    // For example:
                    // onEvent(NavigationHandler.NavigationEvent.PushScreen(RocketDetail(effect.rocketId)))
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading && state.rockets.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        }

        RocketsList(
            rockets = state.rockets,
            onRocketClick = { rocket ->
                viewModel.setEvent(RocketsViewModel.Event.OnRocketClicked(rocket))
            }
        )
    }
}

@Composable
private fun RocketsList(
    rockets: List<Rocket>,
    onRocketClick: (Rocket) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(rockets) { rocket ->
            RocketItem(
                rocket = rocket,
                onClick = { onRocketClick(rocket) }
            )
        }
    }
}

@Composable
private fun RocketItem(
    rocket: Rocket,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            AsyncImage(
                model = rocket.images.firstOrNull(),
                contentDescription = rocket.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = rocket.name,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "First flight: ${rocket.firstFlight}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
