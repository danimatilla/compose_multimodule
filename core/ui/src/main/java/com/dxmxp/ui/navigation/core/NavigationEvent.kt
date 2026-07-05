package com.dxmxp.ui.navigation.core

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.model.Screen

/**
 * Represents all navigation events that can occur in the application.
 */
sealed interface NavigationEvent {
    data class PushScreen(val screen: Screen) : NavigationEvent
    data class PopScreen(val screen: Screen? = null) : NavigationEvent
    data class SetRootScreen(val screen: Screen) : NavigationEvent

    fun screenOrNull(): Screen? = when (this) {
        is PushScreen -> screen
        is PopScreen -> screen
        is SetRootScreen -> screen
    }

    /**
     * Executes the navigation event on the provided backstack.
     */
    suspend fun handle(backStack: NavBackStack<NavKey>) {
        when (this) {
            is PushScreen -> {
                if (backStack.lastOrNull() != screen) {
                    backStack.add(screen)
                }
            }
            is PopScreen -> {
                screen?.run {
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
                    clear()
                    add(screen)
                }
            }
        }
    }
}
