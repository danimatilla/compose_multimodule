package com.dxmxp.stories.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.NavAction
import com.dxmxp.navigation.core.Navigator
import com.dxmxp.navigation.core.rememberNavigator
import com.dxmxp.navigation.model.Route
import com.dxmxp.navigation.utils.NavigationUtils
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.screens.BottomBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScaffold(
    initialRoute: Route? = null,
    rootNavigator: Navigator = LocalNavigator.current
) {
    val initialStack: List<Route> = remember(initialRoute) {
        if (initialRoute != null && initialRoute != StoriesGraph.Home) {
            listOf(StoriesGraph.Home, initialRoute)
        } else {
            listOf(StoriesGraph.Home)
        }
    }

    val nestedBackStack = rememberNavBackStack(*initialStack.toTypedArray())
    val nestedNavigator = rememberNavigator(nestedBackStack)

    val isScaffoldReady by NavigationUtils.rememberModalContentVisible()

    val navigationBarItems = listOf(
        StoriesGraph.Home to Icons.Default.Home,
        StoriesGraph.Profile to Icons.Default.Person,
    )

    val currentDestination = nestedNavigator.currentDestination

    CompositionLocalProvider(LocalNavigator provides nestedNavigator) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            content = { Icon(Icons.Default.Close, contentDescription = "Close") },
                            onClick = { rootNavigator.navAction(NavAction.Pop) }
                        )
                    }
                )
            },
            bottomBar = {
                BottomBar(
                    bottomBarItems = navigationBarItems,
                    currentDestination = currentDestination,
                    onClickItem = { route ->
                        nestedNavigator.navAction(NavAction.Push(route))
                    }
                )
            },
        ) { innerPadding ->
            AnimatedVisibility(
                visible = isScaffoldReady,
                enter = fadeIn()
            ) {
                StoriesNavDisplay(
                    backStack = nestedBackStack,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
