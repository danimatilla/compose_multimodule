package com.dxmxp.seed

import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.seed.navigation.NavGraph
import com.dxmxp.seed.navigation.routes.DeepLinkHandler
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.NavigationHandler
import com.dxmxp.seed.navigation.routes.SeedGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.theme.SeedTheme

class MainActivity : ComponentActivity() {

    private var intentState by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentState = intent

        enableEdgeToEdge()

        setContent {
            val backStack = rememberNavBackStack(SeedGraph)

            LaunchedEffect(intentState) {
                intentState?.data?.let { uri ->
                    DeepLinkHandler.handleDeepLink(uri)?.let { event ->
                        NavigationHandler.handleEvent(backStack, event)
                    }
                    intentState = null
                }
            }

            SeedTheme {
                Scaffold(
                    bottomBar = { BottomNavigationBar(backStack) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavGraph(
                        backStack = backStack,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentState = intent
    }

    @Composable
    private fun BottomNavigationBar(backStack: NavBackStack<NavKey>) {
        val navigationBarItems = listOf(
            SeedGraph to Icons.Default.Home,
            SeedGraph.Search to Icons.Default.Search,
            SeedGraph.Menu to Icons.Default.Menu,
            StoriesGraph to Icons.Default.AutoStories,
            ProfileGraph to Icons.Default.Person
        )

        val currentDestination = backStack.lastOrNull()

        // Find the most specific match in the navigationBarItems list
        val selectedItem = navigationBarItems.map { it.first }.findLast { screen ->
            screen == currentDestination || (screen is Graph && currentDestination?.let { screen.contains(it) } == true)
        }

        NavigationBar {
            navigationBarItems.forEach { (screen, icon) ->
                val selected = selectedItem == screen
                
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            NavigationHandler.handleEvent(
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
