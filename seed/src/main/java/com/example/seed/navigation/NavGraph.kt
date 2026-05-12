package com.example.seed.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.seed.navigation.screens.DashGraph
import com.example.seed.navigation.screens.ProfileGraph
import com.example.seed.navigation.screens.Screen

@Composable
fun NavGraph(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    val onEvent: (NavigationHandler.NavigationEvent) -> Unit = { event ->
        NavigationHandler.eventHandler(backStack, event)
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            DashGraph.run { registerEntries(onEvent) }
        },
        modifier = modifier,
    )
}
