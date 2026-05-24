package com.dxmxp.stories.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.navigation.helpers.NavigationHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesNavGraph(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    onEvent: (NavigationHandler.NavigationEvent) -> Unit
) {
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            StoriesScaffoldGraph.run {
                registerEntries(onEvent)
                registerCommonEntries(onEvent)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
