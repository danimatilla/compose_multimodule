package com.dxmxp.stories.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.stories.navigation.routes.StoriesGraph

@Composable
fun StoriesNavDisplay(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>
) {
    val entryProvider = remember {
        entryProvider {
            StoriesGraph.run { registerScreens() }
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
        modifier = modifier.fillMaxSize()
    )
}
