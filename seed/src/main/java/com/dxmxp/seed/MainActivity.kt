package com.dxmxp.seed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.LocalRootNavigator
import com.dxmxp.navigation.core.rememberNavigator
import com.dxmxp.navigation.model.Route
import com.dxmxp.seed.navigation.MainNavDisplay
import com.dxmxp.seed.navigation.routes.MainGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.stories.navigation.StoriesScaffold
import com.dxmxp.stories.navigation.routes.StoriesGraph
import com.dxmxp.ui.screens.BottomBar
import com.dxmxp.ui.theme.SeedTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.initialRoute == null
        }

        intentState = intent

        setContent {
            val bottomBarItems = remember {
                listOf(
                    MainGraph.Home to Icons.Default.Home,
                    MainGraph.Search to Icons.Default.Search,
                    MainGraph.Menu to Icons.Default.Menu,
                    StoriesGraph to Icons.Default.AutoStories,
                    ProfileGraph to Icons.Default.Person
                )
            }

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            uiState.initialRoute?.let { initialRoute ->
                val backStack = rememberNavBackStack(initialRoute)
                val navigator = rememberNavigator(backStack)

                val currentDestination = navigator.currentDestination

                LaunchedEffect(currentDestination) {
                    viewModel.setEvent(MainViewModel.Event.OnRouteChanged(currentDestination as? Route))
                }

                LaunchedEffect(intentState) {
                    intentState?.data?.let { uri ->
                        viewModel.setEvent(MainViewModel.Event.HandleDeepLink(uri))
                        intentState = null
                    }
                }

                LaunchedEffect(Unit) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is MainViewModel.Effect.SetRoot -> navigator.setRoot(effect.route)
                        }
                    }
                }

                @Composable
                fun MainScaffold() {
                    Scaffold(
                        bottomBar = {
                            BottomBar(
                                shouldShowBottomBar = uiState.showBottomBar,
                                currentDestination = currentDestination,
                                bottomBarItems = bottomBarItems,
                                onClickItem = { route ->
                                    if (route is StoriesGraph) {
                                        navigator.push(route)
                                    } else {
                                        navigator.setRoot(route)
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        MainNavDisplay(
                            backStack = backStack,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }

                SeedTheme {
                    CompositionLocalProvider(
                        LocalNavigator provides navigator,
                        LocalRootNavigator provides navigator,
                    ) {
                        when (currentDestination) {
                            is StoriesGraph -> StoriesScaffold()
                            else -> MainScaffold()
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentState = intent
    }

    private var intentState by mutableStateOf<Intent?>(null)
}
