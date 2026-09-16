package com.dxmxp.seed.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.seed.navigation.routes.AuthGraph
import com.dxmxp.seed.navigation.routes.MainGraph
import com.dxmxp.ui.common.registerCommonEntries

@Composable
fun MainNavDisplay(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>
) {
    val entryProvider = remember {
        entryProvider {
            registerCommonEntries()
            AuthGraph.run { registerScreens() }
            MainGraph.run { registerScreens() }
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
        modifier = modifier.fillMaxSize()
    )
}
