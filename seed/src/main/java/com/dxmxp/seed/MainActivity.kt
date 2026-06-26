package com.dxmxp.seed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.seed.navigation.MainScaffold
import com.dxmxp.seed.navigation.routes.AuthGraph
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph
import com.dxmxp.stories.navigation.StoriesScaffold
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.common.DataObserver
import com.dxmxp.ui.navigation.Screen.Companion.screenEntry
import com.dxmxp.ui.navigation.helpers.DeepLinkHandler
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.ui.navigation.helpers.NavigationUtils.modalAnimation
import com.dxmxp.ui.theme.SeedTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataObserver: DataObserver

    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        intentState = intent

        setContent {
            val scope = rememberCoroutineScope()
            // Start with AuthGraph to perform session check
            val backStack = rememberNavBackStack(AuthGraph)

            val onEvent: (NavigationHandler.NavigationEvent) -> Unit = remember(backStack) {
                { event ->
                    scope.launch {
                        NavigationHandler.handleEvent(
                            backStack = backStack,
                            event = event,
                            dataObserver = dataObserver
                        )
                    }
                }
            }

            val entryProvider = remember(onEvent) {
                entryProvider {
                    // Auth entries
                    AuthGraph.run { registerEntries(onEvent) }

                    screenEntry<MainScaffoldGraph> { MainScaffold(onParentEvent = onEvent) }
                    // Include StoriesScaffoldGraph in entryProvider,
                    // allowing it to be displayed as a modal over the main scaffold.
                    screenEntry<StoriesScaffoldGraph>(
                        metadata = metadata { modalAnimation() }
                    ) { StoriesScaffold(onParentEvent = onEvent) }
                }
            }

            LaunchedEffect(intentState) {
                intentState?.data?.let { uri ->
                    deepLinkHandler.handle(uri)?.let { event ->
                        onEvent(event)
                    }
                    intentState = null
                }
            }

            SeedTheme {
                NavDisplay(
                    backStack = backStack,
                    entryProvider = entryProvider,
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
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