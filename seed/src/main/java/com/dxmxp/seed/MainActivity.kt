package com.dxmxp.seed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.domain.repository.AuthRepository
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.LocalRootNavigator
import com.dxmxp.navigation.core.RouteRegistry
import com.dxmxp.navigation.core.rememberNavigator
import com.dxmxp.navigation.utils.DeepLinkHandler
import com.dxmxp.seed.navigation.MainNavDisplay
import com.dxmxp.seed.navigation.routes.AuthGraph
import com.dxmxp.seed.navigation.routes.MainGraph
import com.dxmxp.ui.theme.SeedTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        intentState = intent

        setContent {
            val hasSession = remember { authRepository.getAccessToken() != null }
            val initialRoute = remember {
                if (hasSession) MainGraph else AuthGraph
            }
            val backStack = rememberNavBackStack(initialRoute)
            val navigator = rememberNavigator(backStack)

            LaunchedEffect(intentState) {
                intentState?.data?.let { uri ->
                    deepLinkHandler.handle(uri)?.let { route ->
                        navigator.push(route)
                    }
                    intentState = null
                }
            }

            SeedTheme {
                CompositionLocalProvider(
                    LocalNavigator provides navigator,
                    LocalRootNavigator provides navigator,
                ) {
                    MainNavDisplay(
                        backStack = backStack,
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
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
