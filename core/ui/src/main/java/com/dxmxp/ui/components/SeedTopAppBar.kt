package com.dxmxp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dxmxp.navigation.core.LocalNavigator
import com.dxmxp.navigation.core.NavAction
import com.dxmxp.navigation.core.Navigator
import com.dxmxp.navigation.model.Route

/**
 * Dynamic TopAppBar wrapper that configures its navigation icon based on navigation state.
 *
 * If the current route is a modal ([Route.isModal]), it displays a Close ("X") icon.
 * Otherwise, if popping is possible ([Navigator.canPop]), it displays a Back arrow icon.
 * If neither condition is met, no navigation icon is shown.
 *
 * Clicking the navigation icon executes [onNavigationClick] or dispatches [NavAction.Pop] via [navigator].
 *
 * @param modifier Modifier for the TopAppBar.
 * @param title Content function for the top app bar title.
 * @param navigator The [Navigator] instance used to resolve navigation state and actions.
 * @param currentRoute The current destination route. Defaults to [Navigator.currentDestination].
 * @param canPop Whether a back/pop navigation is available. Defaults to [Navigator.canPop].
 * @param isModal Whether the current route is a modal route. Defaults to checking [Route.isModal] on [currentRoute].
 * @param onNavigationClick Optional custom callback when the navigation icon is clicked. Defaults to dispatching [NavAction.Pop].
 * @param navigationIcon Optional custom composable for the navigation icon. Overrides automatic icon resolution if provided.
 * @param actions Actions displayed on the end side of the TopAppBar.
 * @param windowInsets Window insets for the TopAppBar.
 * @param colors Colors used for the TopAppBar.
 * @param scrollBehavior Scroll behavior associated with the TopAppBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeedTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigator: Navigator = LocalNavigator.current,
    currentRoute: Route? = navigator.currentDestination as? Route,
    canPop: Boolean = navigator.canPop,
    isModal: Boolean = Route.isModal(currentRoute) || Route.isModal(navigator.parent?.currentDestination as? Route),
    onNavigationClick: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val boundIsModal = remember { isModal }
    val boundCanPop = remember { canPop }

    val navIconToRender = navIconToRender(
        navigationIcon = navigationIcon,
        boundIsModal = boundIsModal,
        onNavigationClick = onNavigationClick,
        navigator = navigator,
        boundCanPop = boundCanPop
    )

    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navIconToRender ?: {},
        actions = actions,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun navIconToRender(
    navigationIcon: @Composable (() -> Unit)?,
    boundIsModal: Boolean,
    onNavigationClick: (() -> Unit)?,
    navigator: Navigator,
    boundCanPop: Boolean
): @Composable (() -> Unit)? {
    return navigationIcon ?: when {
        boundIsModal -> {
            {
                IconButton(
                    onClick = {
                        onNavigationClick?.invoke() ?: navigator.pop()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                    )
                }
            }
        }

        boundCanPop -> {
            {
                IconButton(
                    onClick = {
                        onNavigationClick?.invoke() ?: navigator.pop()
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        }

        else -> null
    }
}
