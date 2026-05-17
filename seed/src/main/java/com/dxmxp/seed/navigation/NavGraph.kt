package com.dxmxp.seed.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.seed.navigation.routes.SeedGraph
import com.dxmxp.ui.navigation.NavigationHandler
import com.dxmxp.ui.navigation.Screen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    onEvent: (NavigationHandler.NavigationEvent) -> Unit
) {
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            SeedGraph.run { registerEntries(onEvent) }
        },
        modifier = modifier,
    )
}
