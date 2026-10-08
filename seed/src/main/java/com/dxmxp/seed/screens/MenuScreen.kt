package com.dxmxp.seed.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dxmxp.ui.components.SeedTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        SeedTopAppBar(title = { Text("Menu") })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Menu Screen")
        }
    }
}
