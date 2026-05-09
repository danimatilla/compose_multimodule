package com.example.seed.navigation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.seed.navigation.NavigationHandler
import com.example.seed.screens.profile.AccountScreen
import com.example.seed.screens.profile.ProfileScreen
import com.example.seed.screens.profile.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object ProfileGraph : Graph  {

    @Composable
    fun ProfileNavGraph(
        modifier: Modifier = Modifier,
        onEvent: (NavigationHandler.NavigationEvent) -> Unit,
        initialScreen: Screen = ProfileGraph
    ) {
        val backStack = rememberNavBackStack(ProfileGraph)

        // Si la pantalla inicial no es la raíz, agregarla al backStack
        if (initialScreen != ProfileGraph) {
            backStack.add(initialScreen)
        }

        NavDisplay(
            entryProvider = entryProvider {
                entry<ProfileGraph> {
                    ProfileScreen { event ->
                        NavigationHandler.eventHandler(
                            backStack = backStack,
                            event = event
                        )
                    }
                }
                entry<Settings> {
                    SettingsScreen { event ->
                        NavigationHandler.eventHandler(
                            backStack = backStack,
                            event = event
                        )
                    }
                }
                entry<Account> {
                    AccountScreen { event ->
                        NavigationHandler.eventHandler(
                            backStack = backStack,
                            event = event
                        )
                    }
                }
            },
            onBack = {
                onEvent(NavigationHandler.NavigationEvent.PopScreen())
            },
            backStack = backStack,
            modifier = modifier
        )
    }

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Account : Screen

    @Composable
    override fun NavDisplay(onEvent: (NavigationHandler.NavigationEvent) -> Unit) {
        val initialScreen = NavigationHandler.getInitialScreen(this)
        ProfileNavGraph(onEvent = onEvent, initialScreen = initialScreen)
    }
}

