package com.dxmxp.seed.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.seed.navigation.routes.AuthGraph
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>
) {
    val entryProvider = remember {
        entryProvider {
            AuthGraph.run { registerEntries() }
            MainScaffoldGraph.run { registerEntries() }
            ProfileGraph.run { registerEntries() }
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
        modifier = modifier.fillMaxSize()
    )
}
