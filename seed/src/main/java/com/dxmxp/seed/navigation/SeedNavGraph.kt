package com.dxmxp.seed.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay

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
    val isModal = currentDestination?.belongModalGraph() ?: false
    var animateBottomBar by remember { mutableStateOf(!isModal) }
    LaunchedEffect(isModal) {
        animateBottomBar = !isModal
    }

    SeedScaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = animateBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomBar(
                    shouldShowBottomBar = !isModal,
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
