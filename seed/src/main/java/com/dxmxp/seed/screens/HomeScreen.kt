package com.dxmxp.seed.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.NavAction
import com.dxmxp.seed.navigation.routes.MainGraph
import com.dxmxp.ui.components.SeedTopAppBar
import com.dxmxp.ui.screens.WebView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val navigator = LocalNavigator.current

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        SeedTopAppBar(
            title = { Text("Home") },
            currentRoute = MainGraph.Home,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Home Screen")
            Button(
                onClick = {
                    val route = WebView(url = "https://www.google.com")
                    navigator.navAction(NavAction.Push(route))
                },
                content = { Text("WebView") },
            )
        }
    }
}
