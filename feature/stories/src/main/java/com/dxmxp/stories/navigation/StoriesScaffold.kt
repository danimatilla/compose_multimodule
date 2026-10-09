package com.dxmxp.stories.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.NavAction
import com.dxmxp.navigation.core.Navigator
import com.dxmxp.navigation.core.rememberNavigator
import com.dxmxp.navigation.model.Route
import com.dxmxp.navigation.ui.NavGraphDisplay
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.common.registerCommonScreens
import com.dxmxp.ui.components.BottomBar


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
    val nestedNavigator = rememberNavigator(nestedBackStack, parent = rootNavigator)

    val navigationBarItems = listOf(
        StoriesGraph.Home to Icons.Default.Home,
        StoriesGraph.Profile to Icons.Default.Person,
    )

    val currentDestination = nestedNavigator.currentDestination

    CompositionLocalProvider(LocalNavigator provides nestedNavigator) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                BottomBar(
                    bottomBarItems = navigationBarItems,
                    currentDestination = currentDestination,
                    onClickItem = { route ->
                        nestedNavigator.navAction(NavAction.Root(route))
                    }
                )
            },
        ) { innerPadding ->
            NavGraphDisplay(
                backStack = nestedBackStack,
                modifier = Modifier.padding(innerPadding)
            ) {
                registerCommonScreens()
                StoriesGraph.Default.run { registerScreens() }
            }
        }
    }
}
