package com.dxmxp.seed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import dagger.hilt.android.AndroidEntryPoint
import com.dxmxp.seed.navigation.SeedNavGraph
import com.dxmxp.seed.navigation.routes.DeepLinkHandler
import com.dxmxp.seed.navigation.routes.SeedGraph
import com.dxmxp.theme.SeedTheme
import com.dxmxp.ui.base.DataObserver
import com.dxmxp.ui.navigation.helpers.NavigationHandler
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
            val backStack = rememberNavBackStack(SeedGraph)
            val scope = rememberCoroutineScope()

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
                SeedNavGraph(
                    backStack = backStack,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxSize()
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
