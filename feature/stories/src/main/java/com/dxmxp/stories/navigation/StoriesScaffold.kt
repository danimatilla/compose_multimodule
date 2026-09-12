package com.dxmxp.stories.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.rememberNavigator
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.screens.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScaffold() {
    val rootNavigator = LocalNavigator.current
    
    val nestedBackStack = rememberNavBackStack(StoriesGraph.Feed)
    val nestedNavigator = rememberNavigator(nestedBackStack)

    val navigationBarItems = remember {
        listOf(
            StoriesGraph.Feed to Icons.Default.Home,
            StoriesGraph.Notifications to Icons.Default.Notifications,
            StoriesGraph.Profile to Icons.Default.Person,
        )
    }
    
    val currentDestination = nestedNavigator.currentDestination

    CompositionLocalProvider(LocalNavigator provides nestedNavigator) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            content = { Icon(Icons.Default.Close, contentDescription = "Close") },
                            onClick = { rootNavigator.pop() }
                        )
                    }
                )
            },
            bottomBar = {
                BottomBar(
                    bottomBarItems = navigationBarItems,
                    currentDestination = currentDestination,
                    onClickItem = { route ->
                        nestedNavigator.setRoot(route)
                    }
                )
            },
        ) { innerPadding ->
            StoriesNavDisplay(
                backStack = nestedBackStack,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
