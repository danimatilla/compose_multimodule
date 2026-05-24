package com.dxmxp.seed.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.Scene
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.base.DataObserverEntryPoint
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.ui.screens.BottomBar
import com.dxmxp.ui.screens.SeedScaffold
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(
    onParentEvent: (NavigationHandler.NavigationEvent) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(MainScaffoldGraph)
    val dataObserver = remember {
        EntryPointAccessors
            .fromApplication(
                context = context,
                entryPoint = DataObserverEntryPoint::class.java
            )
            .dataObserver()
    }

    val onEvent: (NavigationHandler.NavigationEvent) -> Unit = remember(backStack) {
        { event ->
            scope.launch {
                NavigationHandler.handleEvent(backStack, event, dataObserver)
            }
        }
    }

    val bottomBarItems = listOf(
        MainScaffoldGraph to Icons.Default.Home,
        MainScaffoldGraph.Search to Icons.Default.Search,
        MainScaffoldGraph.Menu to Icons.Default.Menu,
        StoriesScaffoldGraph to Icons.Default.AutoStories,
        ProfileGraph to Icons.Default.Person
    )
    val currentDestination = backStack.lastOrNull()
    val shouldShowBottomBar = (currentDestination as? Screen)?.showMainBottomBar ?: true

    SeedScaffold(
        bottomBar = {
            BottomBar(
                shouldShowBottomBar = shouldShowBottomBar,
                bottomBarItems = bottomBarItems,
                currentDestination = currentDestination,
                onClickItem = { screen ->
                    if (screen == StoriesScaffoldGraph) {
                        onParentEvent(NavigationHandler.NavigationEvent.PushScreen(screen))
                    } else {
                        onEvent(NavigationHandler.NavigationEvent.SetRootScreen(screen))
                    }
                }
            )
        }
    ) { innerPaddings ->
        MainNavGraph(
            backStack = backStack,
            onEvent = onEvent,
            modifier = Modifier.padding(innerPaddings)
        )
    }
}
