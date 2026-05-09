package com.example.seed.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.seed.navigation.screens.ProfileGraph
import com.example.seed.navigation.screens.Screen
import com.example.seed.screens.HomeScreen

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Screen.Home)

    NavDisplay(
        entryProvider = entryProvider {
            entry<Screen.Home> {
                HomeScreen { event ->
                    NavigationHandler.eventHandler(
                        backStack = backStack,
                        event = event
                    )
                }
            }
            entry<ProfileGraph> {
                ProfileGraph.NavDisplay { event ->
                    NavigationHandler.eventHandler(
                        backStack = backStack,
                        event = event
                    )
                }
            }
        },
        backStack = backStack,
        modifier = modifier,
    )
}
