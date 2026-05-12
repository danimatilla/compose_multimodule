package com.example.seed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AmpStories
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
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
import com.example.seed.navigation.NavGraph
import com.example.seed.navigation.NavigationHandler
import com.example.seed.navigation.screens.DashGraph
import com.example.seed.navigation.screens.ProfileGraph
import com.example.ui.theme.SeedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val backStack = rememberNavBackStack(DashGraph.Home)

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
            DashGraph.Home to Icons.Default.Home,
            DashGraph.Search to Icons.Default.Search,
            DashGraph.Menu to Icons.Default.Menu,
            DashGraph.Stories to Icons.Default.AutoStories,
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
