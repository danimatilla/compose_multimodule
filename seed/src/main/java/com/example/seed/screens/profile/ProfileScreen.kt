package com.example.seed.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.seed.navigation.NavigationHandler
import com.example.seed.navigation.screens.ProfileGraph


@Composable
fun ProfileScreen(onEvent: (NavigationHandler.NavigationEvent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile Screen")
        Button(onClick = { onEvent(NavigationHandler.NavigationEvent.PushScreen(ProfileGraph.Settings)) }) {
            Text("Go to Settings")
        }
        Button(onClick = { onEvent(NavigationHandler.NavigationEvent.PushScreen(ProfileGraph.Account)) }) {
            Text("Go to Account")
        }
        Button(onClick = { onEvent(NavigationHandler.NavigationEvent.PopScreen()) }) {
            Text("Back")
        }
    }
}
