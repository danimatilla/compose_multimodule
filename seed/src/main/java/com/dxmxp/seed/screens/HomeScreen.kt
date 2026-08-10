package com.dxmxp.seed.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dxmxp.ui.navigation.core.LocalNavigator
import com.dxmxp.ui.screens.WebView


@Composable
fun HomeScreen() {
    val navigator = LocalNavigator.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home Screen")
        Button(
            onClick = {
                navigator.push(WebView(url = "https://www.google.com"))
            },
            content = { Text("WebView") }
        )
    }
}
