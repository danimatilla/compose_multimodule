package com.dxmxp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun SeedScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        modifier = modifier
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    shouldShowBottomBar: Boolean = true,
    bottomBarItems: List<Pair<Screen, ImageVector>>,
    currentDestination: NavKey?,
    onClickItem: (Screen) -> Unit
) {
    AnimatedVisibility(
        visible = shouldShowBottomBar,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        // Find the most specific match in the navigationBarItems list
        val selectedItem = bottomBarItems.map { it.first }.findLast { screen ->
            screen == currentDestination || (screen is Graph && currentDestination?.let {
                screen.contains(
                    it
                )
            } == true)
        }

        NavigationBar(
            modifier = modifier
        ) {
            bottomBarItems.forEach { (screen, icon) ->
                val selected = selectedItem == screen

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (selected) return@NavigationBarItem
                        onClickItem(screen)
                    },
                    icon = { Icon(icon, contentDescription = null) }
                )
            }
        }
    }
}
