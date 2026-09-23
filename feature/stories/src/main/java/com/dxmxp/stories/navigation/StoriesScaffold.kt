package com.dxmxp.stories.navigation

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
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.NavAction
import com.dxmxp.navigation.core.Navigator
import com.dxmxp.navigation.core.RouteRegistry
import com.dxmxp.navigation.core.rememberNavigator
import com.dxmxp.navigation.model.Route
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.screens.BottomBar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoriesScaffoldViewModel @Inject constructor(
    val routeRegistry: RouteRegistry
) : ViewModel()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScaffold(
    initialRoute: Route = StoriesGraph.Home,
    rootNavigator: Navigator = LocalNavigator.current,
    viewModel: StoriesScaffoldViewModel = hiltViewModel()
) {
    val nestedBackStack = rememberNavBackStack(initialRoute)
    val nestedNavigator = rememberNavigator(nestedBackStack)

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
            StoriesNavDisplay(
                backStack = nestedBackStack,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
