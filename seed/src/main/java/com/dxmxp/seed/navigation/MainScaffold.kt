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
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.dxmxp.seed.navigation.routes.CatalogGraph
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.navigation.core.LocalNavigator
import com.dxmxp.ui.navigation.orchestration.NavigationOrchestrator
import com.dxmxp.ui.navigation.model.Screen
import com.dxmxp.ui.navigation.scaffold.rememberScaffoldController
import com.dxmxp.ui.screens.BottomBar
import androidx.compose.material.icons.filled.ShoppingCart
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScaffoldViewModel @Inject constructor(
    val orchestrator: NavigationOrchestrator
) : ViewModel()

@Composable
fun MainScaffold() {
    val navigator = LocalNavigator.current
    val scaffoldViewModel: MainScaffoldViewModel = hiltViewModel()

    val controller = rememberScaffoldController(
        initialScreen = MainScaffoldGraph.Home,
        graph = MainScaffoldGraph,
        orchestrator = scaffoldViewModel.orchestrator
    )

    val bottomBarItems = listOf(
        MainScaffoldGraph.Home to Icons.Default.Home,
        CatalogGraph.ProductList to Icons.Default.ShoppingCart,
        MainScaffoldGraph.Search to Icons.Default.Search,
        MainScaffoldGraph.Menu to Icons.Default.Menu,
        StoriesScaffoldGraph to Icons.Default.AutoStories,
        ProfileGraph to Icons.Default.Person
    )
    
    val currentDestination = controller.currentDestination
    val shouldShowBottomBar = (currentDestination as? Screen)?.showMainBottomBar != false

    Scaffold(
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
            backStack = controller.backStack,
            modifier = Modifier.padding(innerPaddings)
        )
    }
}
