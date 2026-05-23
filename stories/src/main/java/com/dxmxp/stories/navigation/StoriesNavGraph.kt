package com.dxmxp.stories.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.dxmxp.stories.navigation.routes.StoriesScaffoldGraph
import com.dxmxp.ui.base.DataObserverEntryPoint
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.ui.screens.BottomBar
import com.dxmxp.ui.screens.SeedScaffold
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

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
            StoriesScaffoldGraph.run { registerEntries(onEvent) }
        },
        modifier = modifier
    )
}
