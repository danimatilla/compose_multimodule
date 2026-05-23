package com.dxmxp.seed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import com.dxmxp.seed.navigation.DeepLinkHandler
import com.dxmxp.seed.navigation.MainScaffold
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph
import com.dxmxp.stories.navigation.StoriesScaffold
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.theme.SeedTheme
import com.dxmxp.ui.base.DataObserver
import com.dxmxp.ui.navigation.Screen.Companion.screenEntry
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.ui.navigation.helpers.NavigationUtils.modalAnimation
import com.dxmxp.ui.screens.WebView
import com.dxmxp.ui.screens.WebViewScreen
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataObserver: DataObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        intentState = intent

        setContent {
            val scope = rememberCoroutineScope()
            val backStack = rememberNavBackStack(MainScaffoldGraph)

            val onEvent: (NavigationHandler.NavigationEvent) -> Unit = { event ->
                scope.launch {
                    NavigationHandler.handleEvent(
                        backStack = backStack,
                        event = event,
                        dataObserver = dataObserver
                    )
                }
            }

            LaunchedEffect(intentState) {
                intentState?.data?.let { uri ->
                    DeepLinkHandler.handleDeepLink(uri)?.let { event ->
                        onEvent(event)
                    }
                    intentState = null
                }
            }

            SeedTheme {
                NavDisplay(
                    backStack = backStack,
                    entryProvider = entryProvider {
                        screenEntry<MainScaffoldGraph> { MainScaffold(onParentEvent = onEvent) }
                        screenEntry<StoriesScaffoldGraph>(
                            metadata = metadata { modalAnimation() }
                        ) { StoriesScaffold (onParentEvent = onEvent) }
                        screenEntry<WebView> { screen -> WebViewScreen(screen, onEvent) }
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentState = intent
    }

    private var intentState by mutableStateOf<Intent?>(null)
}
