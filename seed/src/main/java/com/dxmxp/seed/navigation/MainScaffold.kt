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
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.LocalNavigator
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.screens.BottomBar
import com.dxmxp.ui.screens.SeedScaffold
import dagger.hilt.android.EntryPointAccessors

@Composable
fun MainScaffold() {
    val backStack = rememberNavBackStack(MainScaffoldGraph.Home)
    val navigator = LocalNavigator.current

    Graph.HandleGraphEvents(backStack = backStack, graph = MainScaffoldGraph)

    val bottomBarItems = listOf(
        MainScaffoldGraph.Home to Icons.Default.Home,
        MainScaffoldGraph.Search to Icons.Default.Search,
        MainScaffoldGraph.Menu to Icons.Default.Menu,
        StoriesScaffoldGraph to Icons.Default.AutoStories,
        ProfileGraph to Icons.Default.Person
    )
    val currentDestination = backStack.lastOrNull()
    val shouldShowBottomBar = (currentDestination as? Screen)?.showMainBottomBar != false

    SeedScaffold(
        bottomBar = {
            BottomBar(
                shouldShowBottomBar = shouldShowBottomBar,
                bottomBarItems = bottomBarItems,
                currentDestination = currentDestination,
                onClickItem = { screen ->
                    if (screen == StoriesScaffoldGraph) {
                        navigator.push(screen)
                    } else {
                        navigator.setRoot(screen)
                    }
                }
            )
        }
    ) { innerPaddings ->
        MainNavGraph(
            backStack = backStack,
            modifier = Modifier.padding(innerPaddings)
        )
    }
}
