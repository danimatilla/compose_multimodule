package com.dxmxp.seed.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.seed.navigation.routes.SeedGraph
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.ui.screens.BottomBar
import com.dxmxp.ui.screens.SeedScaffold

@Composable
fun SeedNavGraph(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    onEvent: (NavigationHandler.NavigationEvent) -> Unit
) {
    val navigationBarItems = remember {
        listOf(
            SeedGraph to Icons.Default.Home,
            SeedGraph.Search to Icons.Default.Search,
            SeedGraph.Menu to Icons.Default.Menu,
            StoriesGraph to Icons.Default.AutoStories,
            ProfileGraph to Icons.Default.Person
        )
    }
    val currentDestination = backStack.lastOrNull() as? Screen
    val shouldShowBottomBar = currentDestination?.inheritedShowSeedBottomBar == true

    SeedScaffold(
        topBar = { /* You can add a top bar here if needed */ },
        bottomBar = {
            BottomBar(
                shouldShowBottomBar = shouldShowBottomBar,
                navigationBarItems = navigationBarItems,
                backStack = backStack,
                onClickItem = { screen ->
                    if (screen == StoriesGraph) {
                        onEvent(NavigationHandler.NavigationEvent.PushScreen(screen))
                    } else {
                        onEvent(NavigationHandler.NavigationEvent.SetRootScreen(screen))
                    }
                }
            )
        }
    ) {
        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider {
                SeedGraph.run { registerEntries(onEvent) }
            },
            modifier = modifier,
        )
    }
}
