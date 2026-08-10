package com.dxmxp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.model.Graph
import com.dxmxp.ui.navigation.model.Route
import com.dxmxp.ui.navigation.model.Screen

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    shouldShowBottomBar: Boolean = true,
    bottomBarItems: List<Pair<Route, ImageVector>>,
    currentDestination: NavKey?,
    onClickItem: (Route) -> Unit
) {
    AnimatedVisibility(
        visible = shouldShowBottomBar,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        // Find the most specific match in the navigationBarItems list
        val selectedItem = bottomBarItems.map { it.first }.findLast { route ->
            route == currentDestination || (route is Graph && currentDestination?.let {
                route.contains(
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
