package com.dxmxp.seed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.LocalRootNavigator
import com.dxmxp.navigation.core.rememberNavigator
import com.dxmxp.seed.navigation.MainNavDisplay
import com.dxmxp.ui.theme.SeedTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.initialRoute == null
        }
        
        intentState = intent

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            uiState.initialRoute?.let { initialRoute ->
                val backStack = rememberNavBackStack(initialRoute)
                val navigator = rememberNavigator(backStack)

                LaunchedEffect(intentState) {
                    intentState?.data?.let { uri ->
                        viewModel.setEvent(MainViewModel.Event.HandleDeepLink(uri))
                        intentState = null
                    }
                }

                LaunchedEffect(Unit) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is MainViewModel.Effect.NavigateTo -> navigator.push(effect.route)
                        }
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
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentState = intent
    }

    private var intentState by mutableStateOf<Intent?>(null)
}
