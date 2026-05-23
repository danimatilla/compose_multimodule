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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.base.DataObserverEntryPoint
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.ui.screens.BottomBar
import com.dxmxp.ui.screens.SeedScaffold
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScaffold(
    onParentEvent: (NavigationHandler.NavigationEvent) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(StoriesScaffoldGraph)

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

    val navigationBarItems = remember {
        listOf(
            StoriesScaffoldGraph to Icons.Default.Home,
            StoriesScaffoldGraph.Notifications to Icons.Default.Notifications,
            StoriesScaffoldGraph.Profile to Icons.Default.Person,
        )
    }

    SeedScaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        content = { Icon(Icons.Default.Close, contentDescription = "Close") },
                        onClick = { onParentEvent(NavigationHandler.NavigationEvent.PopScreen()) }
                    )
                }
            )
        },
        bottomBar = {
            BottomBar(
                bottomBarItems = navigationBarItems,
                currentDestination = backStack.lastOrNull(),
                onClickItem = { screen ->
                    onChildEvent(NavigationHandler.NavigationEvent.PushScreen(screen))
                }
            )
        },
    ) { innerPadding ->
        StoriesNavGraph(
            backStack = backStack,
            onEvent = onChildEvent,
            modifier = Modifier.padding(innerPadding),
        )
    }

}