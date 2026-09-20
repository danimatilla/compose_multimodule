package com.dxmxp.seed.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.rememberNavigator
import com.dxmxp.navigation.model.Route
import com.dxmxp.seed.navigation.routes.MainGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.screens.BottomBar

@Composable
fun MainScaffold() {
    val rootNavigator = LocalNavigator.current
    val rootDestination = rootNavigator.currentDestination

    val initialNestedRoute = remember(rootDestination) {
        when (rootDestination) {
            is MainGraph -> MainGraph.Home
            else -> rootDestination ?: MainGraph.Home
        }
    }
    
    // Nested backstack for the main content area
    val nestedBackStack = rememberNavBackStack(initialNestedRoute)
    val nestedNavigator = rememberNavigator(nestedBackStack)

    val bottomBarItems = listOf(
        MainGraph.Home to Icons.Default.Home,
        MainGraph.Search to Icons.Default.Search,
        MainGraph.Menu to Icons.Default.Menu,
        StoriesGraph to Icons.Default.AutoStories,
        ProfileGraph to Icons.Default.Person
    )
    
    val currentDestination = nestedNavigator.currentDestination
    val shouldShowBottomBar = (currentDestination as? Route)?.showMainBottomBar != false

    CompositionLocalProvider(LocalNavigator provides nestedNavigator) {
        Scaffold(
            bottomBar = {
                BottomBar(
                    shouldShowBottomBar = shouldShowBottomBar,
                    bottomBarItems = bottomBarItems,
                    currentDestination = currentDestination,
                    onClickItem = { route ->
                        when (route) {
                            is StoriesGraph -> rootNavigator.push(route)
                            is ProfileGraph -> nestedNavigator.setRoot(route)
                            else -> nestedNavigator.setRoot(route)
                        }
                    }
                )
            }
        ) { innerPaddings ->
            MainNavDisplay(
                backStack = nestedBackStack,
                modifier = Modifier.padding(innerPaddings)
            )
        }
    }
}
