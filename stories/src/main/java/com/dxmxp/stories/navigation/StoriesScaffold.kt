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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.LocalNavigator
import com.dxmxp.ui.screens.BottomBar
import com.dxmxp.ui.screens.SeedScaffold
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScaffold() {
    val backStack = rememberNavBackStack(StoriesScaffoldGraph.Feed)
    val navigator = LocalNavigator.current

    Graph.HandleGraphEvents(backStack = backStack, graph = StoriesScaffoldGraph)

    val navigationBarItems = remember {
        listOf(
            StoriesScaffoldGraph.Feed to Icons.Default.Home,
            StoriesScaffoldGraph.Notifications to Icons.Default.Notifications,
            StoriesScaffoldGraph.Profile to Icons.Default.Person,
        )
    }
    val currentDestination = backStack.lastOrNull()

    SeedScaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        content = { Icon(Icons.Default.Close, contentDescription = "Close") },
                        onClick = { navigator.pop() }
                    )
                }
            )
        },
        bottomBar = {
            BottomBar(
                bottomBarItems = navigationBarItems,
                currentDestination = currentDestination,
                onClickItem = { screen ->
                    navigator.setRoot(screen)
                }
            )
        },
    ) { innerPadding ->
        StoriesNavGraph(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
