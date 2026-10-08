package com.dxmxp.seed.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.ui.components.SeedTopAppBar

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
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
                is ProfileViewModel.Effect.Navigate -> navigator.navAction(effect.action)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: ProfileViewModel.State,
    onEvent: (ProfileViewModel.Event) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        SeedTopAppBar(title = { Text("Profile") })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Profile Screen")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onEvent(ProfileViewModel.Event.OnLogoutClick) },
                enabled = state.isLoading == false,
            ) {
                Text("Logout")
            }
        }
    }
}
