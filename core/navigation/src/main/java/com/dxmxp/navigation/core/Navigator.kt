package com.dxmxp.navigation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.model.Route

/**
 * A simple navigator that wraps NavBackStack to provide navigation operations.
 */
class Navigator(private val backStack: NavBackStack<NavKey>) {

    fun push(route: Route) {
        if (backStack.lastOrNull() != route) {
            backStack.add(route)
        }
    }

    fun pop() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    fun popTo(route: Route) {
        val index = backStack.indexOfLast { it == route }
        if (index != -1) {
            backStack.subList(index + 1, backStack.size).clear()
        }
    }

    fun setRoot(route: Route) {
        backStack.run {
            if (lastOrNull() == route) return@run
            clear()
            add(route)
        }
    }

    val currentDestination: NavKey?
        get() = backStack.lastOrNull()
}

@Composable
fun rememberNavigator(backStack: NavBackStack<NavKey>): Navigator {
    return remember(backStack) { Navigator(backStack) }
}

val LocalNavigator: ProvidableCompositionLocal<Navigator> = staticCompositionLocalOf {
    error("No Navigator provided")
}

val LocalRootNavigator: ProvidableCompositionLocal<Navigator> = staticCompositionLocalOf {
    error("No Root Navigator provided")
}
