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
import com.example.seed.navigation.screens.DashGraph
import com.example.seed.navigation.screens.Screen


@Composable
fun SettingsScreen(onEvent: (NavigationHandler.NavigationEvent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings Screen")
        Button(onClick = { onEvent(NavigationHandler.NavigationEvent.PopScreen()) }) {
            Text("Back to Profile")
        }
        Button(onClick = { onEvent(NavigationHandler.NavigationEvent.PopScreen(DashGraph.Home)) }) {
            Text("Back to Home")
        }
    }
}
