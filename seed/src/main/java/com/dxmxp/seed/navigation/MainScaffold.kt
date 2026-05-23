package com.dxmxp.seed.navigation

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
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph.Menu
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph.Search
import com.dxmxp.seed.screens.HomeScreen
import com.dxmxp.seed.screens.MenuScreen
import com.dxmxp.seed.screens.SearchScreen
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.base.DataObserverEntryPoint
import com.dxmxp.ui.navigation.Screen.Companion.screenEntry
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.ui.screens.BottomBar
import com.dxmxp.ui.screens.SeedScaffold
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(
    onParentEvent: (NavigationHandler.NavigationEvent) -> Unit,
) {
    val backStack = rememberNavBackStack(MainScaffoldGraph)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataObserver = remember {
        EntryPointAccessors
            .fromApplication(
                context = context,
                entryPoint = DataObserverEntryPoint::class.java
            )
            .dataObserver()
    }

    val onChildEvent: (NavigationHandler.NavigationEvent) -> Unit = { event ->
        scope.launch {
            NavigationHandler.handleEvent(backStack, event, dataObserver)
        }
    }

    val bottomBarItems = listOf(
        MainScaffoldGraph to Icons.Default.Home,
        Search to Icons.Default.Search,
        Menu to Icons.Default.Menu,
        StoriesScaffoldGraph to Icons.Default.AutoStories,
        ProfileGraph to Icons.Default.Person
    )
    val currentDestination = backStack.lastOrNull()

    SeedScaffold(
        bottomBar = {
            BottomBar(
                bottomBarItems = bottomBarItems,
                currentDestination = currentDestination,
                onClickItem = { screen ->
                    if (screen == StoriesScaffoldGraph) {
                        onParentEvent(NavigationHandler.NavigationEvent.PushScreen(screen))
                    } else {
                        onChildEvent(NavigationHandler.NavigationEvent.PushScreen(screen))
                    }
                }
            )
        }
    ) { innerPaddings ->
        MainNavGraph(
            backStack = backStack,
            onEvent = onChildEvent,
            modifier = Modifier.padding(innerPaddings)
        )
    }
}
