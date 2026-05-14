package com.dxmxp.seed.screens.dash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dxmxp.ui.navigation.NavigationHandler
import com.dxmxp.ui.screens.WebView


@Composable
fun HomeScreen(onEvent: (NavigationHandler.NavigationEvent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home Screen")
        Button(
            onClick = {
                onEvent(
                    NavigationHandler.NavigationEvent.PushScreen(
                        WebView(url = "https://www.inditex.com")
                    )
                )
            },
            content = { Text("WebView") }
        )
    }
}
