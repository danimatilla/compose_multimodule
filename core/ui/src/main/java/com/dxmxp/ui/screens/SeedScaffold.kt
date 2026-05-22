package com.dxmxp.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.Screen

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
    shouldShowBottomBar: Boolean,
    navigationBarItems: List<Pair<Screen, ImageVector>>,
    backStack: NavBackStack<NavKey>,
    onClickItem: (Screen) -> Unit
) {
    if (!shouldShowBottomBar) return

    val currentDestination = backStack.lastOrNull()

    // Find the most specific match in the navigationBarItems list
    val selectedItem = navigationBarItems.map { it.first }.findLast { screen ->
        screen == currentDestination || (screen is Graph && currentDestination?.let {
            screen.contains(
                it
            )
        } == true)
    }

    NavigationBar {
        navigationBarItems.forEach { (screen, icon) ->
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
