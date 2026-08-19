package com.dxmxp.ui.navigation.core

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.model.Route

/**
 * Represents all navigation events that can occur in the application.
 */
sealed interface NavigationEvent {
    data class PushScreen(val route: Route) : NavigationEvent
    data class PopScreen(val route: Route? = null) : NavigationEvent
    data class SetRootScreen(val route: Route) : NavigationEvent

    fun routeOrNull(): Route? = when (this) {
        is PushScreen -> route
        is PopScreen -> route
        is SetRootScreen -> route
    }

    fun logString(): String {
        val route = routeOrNull()
        val routeName = route?.route ?: route?.javaClass?.simpleName ?: ""
        return when (this) {
            is PushScreen -> "PUSH($routeName)"
            is PopScreen -> "POP" + (route?.let { "($routeName)" } ?: "")
            is SetRootScreen -> "SET_ROOT($routeName)"
        }
    }

    /**
     * Executes the navigation event on the provided backstack.
     */
    suspend fun handle(backStack: NavBackStack<NavKey>) {
        when (this) {
            is PushScreen -> {
                if (backStack.lastOrNull() != route) {
                    backStack.add(route)
                }
            }
            is PopScreen -> {
                route?.run {
                    backStack.indexOfLast { it == this }
                        .takeIf { it != -1 }
                        ?.let { targetIndex ->
                            backStack.subList(targetIndex + 1, backStack.size).clear()
                        }
                } ?: run {
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                }
            }
            is SetRootScreen -> {
                backStack.run {
                    if (lastOrNull() == route) return@run
                    clear()
                    add(route)
                }
            }
        }
    }
}
