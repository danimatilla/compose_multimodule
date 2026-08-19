package com.dxmxp.seed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.seed.navigation.MainScaffold
import com.dxmxp.seed.navigation.routes.AuthGraph
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph
import com.dxmxp.seed.screens.login.LoginScreen
import com.dxmxp.stories.navigation.StoriesScaffold
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.navigation.core.LocalNavigator
import com.dxmxp.ui.navigation.core.NavigationManager
import com.dxmxp.ui.navigation.core.NavigationManagerBridge
import com.dxmxp.ui.navigation.model.Graph
import com.dxmxp.ui.navigation.model.Graph.Companion.registerGraphs
import com.dxmxp.ui.navigation.model.Screen.Companion.screenEntry
import com.dxmxp.ui.navigation.orchestration.NavigationOrchestrator
import com.dxmxp.ui.navigation.utils.DeepLinkHandler
import com.dxmxp.ui.navigation.utils.NavigationUtils.modalAnimation
import com.dxmxp.ui.theme.SeedTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var navigationOrchestrator: NavigationOrchestrator

    @Inject
    lateinit var graphs: Set<@JvmSuppressWildcards Graph>

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        intentState = intent

        setContent {
            val initialRoute = remember {
                if (navigationOrchestrator.hasActiveSession()) MainScaffoldGraph else AuthGraph
            }
            val backStack = rememberNavBackStack(initialRoute)

            val navigator = remember {
                NavigationManagerBridge(navigationManager)
            }

            LaunchedEffect(Unit) {
                navigationOrchestrator.events.collect { event ->
                    navigationOrchestrator.handleEventForBackstack(backStack, event)
                }
            }

            val entryProvider = remember {
                entryProvider {
                    registerGraphs(graphs)
                }
            }

            LaunchedEffect(intentState) {
                intentState?.data?.let { uri ->
                    deepLinkHandler.handle(uri)?.let { event ->
                        navigationManager.navigate(event)
                    }
                    intentState = null
                }
            }

            SeedTheme {
                CompositionLocalProvider(
                    LocalNavigator provides navigator,
                ) {
                    NavDisplay(
                        backStack = backStack,
                        entryProvider = entryProvider,
                        transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) },
                        popTransitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.background),
                    )
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
