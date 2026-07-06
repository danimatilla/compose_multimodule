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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.navigation.core.LocalNavigator
import com.dxmxp.ui.navigation.orchestration.NavigationOrchestrator
import com.dxmxp.ui.navigation.scaffold.rememberScaffoldController
import com.dxmxp.ui.screens.BottomBar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoriesScaffoldViewModel @Inject constructor(
    val orchestrator: NavigationOrchestrator
) : ViewModel()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScaffold() {
    val navigator = LocalNavigator.current
    val scaffoldViewModel: StoriesScaffoldViewModel = hiltViewModel()

    val controller = rememberScaffoldController(
        initialScreen = StoriesScaffoldGraph.Feed,
        graph = StoriesScaffoldGraph,
        orchestrator = scaffoldViewModel.orchestrator
    )

    val navigationBarItems = remember {
        listOf(
            StoriesScaffoldGraph.Feed to Icons.Default.Home,
            StoriesScaffoldGraph.Notifications to Icons.Default.Notifications,
            StoriesScaffoldGraph.Profile to Icons.Default.Person,
        )
    }
    
    val currentDestination = controller.currentDestination

    Scaffold(
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
            backStack = controller.backStack,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
