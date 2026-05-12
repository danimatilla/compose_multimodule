package com.dxmxp.seed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.seed.navigation.NavGraph
import com.dxmxp.ui.navigation.NavigationHandler
import com.dxmxp.seed.navigation.routes.SeedGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.theme.SeedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val backStack = rememberNavBackStack(SeedGraph)

            SeedTheme {
                Scaffold(
                    bottomBar = { BottomNavigationBar(backStack) }
                ) { paddingValues ->
                    NavGraph(
                        backStack = backStack,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }

    @Composable
    private fun BottomNavigationBar(backStack: NavBackStack<NavKey>) {
        val items = listOf(
            SeedGraph to Icons.Default.Home,
            SeedGraph.Search to Icons.Default.Search,
            SeedGraph.Menu to Icons.Default.Menu,
            StoriesGraph to Icons.Default.AutoStories,
            ProfileGraph to Icons.Default.Person
        )

        val currentDestination = backStack.lastOrNull()

        NavigationBar {
            items.forEach { (screen, icon) ->
                NavigationBarItem(
                    selected = currentDestination == screen,
                    onClick = {
                        if (currentDestination != screen) {
                            NavigationHandler.eventHandler(
                                backStack,
                                NavigationHandler.NavigationEvent.SetRootScreen(screen)
                            )
                        }
                    },
                    icon = { Icon(icon, contentDescription = null) }
                )
            }
        }
    }
}
