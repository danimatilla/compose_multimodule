package com.dxmxp.seed.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.ui.components.SeedTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        SeedTopAppBar(
            title = { Text("Settings") },
            currentRoute = ProfileGraph.Settings,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Settings Screen")
        }
    }
}
