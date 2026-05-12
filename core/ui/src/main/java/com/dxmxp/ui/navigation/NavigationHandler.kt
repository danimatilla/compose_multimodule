package com.dxmxp.ui.navigation

import android.util.Log
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlin.jvm.javaClass

object NavigationHandler {

    fun eventHandler(
        backStack: NavBackStack<NavKey>,
        event: NavigationEvent
    ) {
        var eventString: String
        val initialBackStack = backStack.stackString()

        when (event) {
            is NavigationEvent.PushScreen -> {
                backStack.add(event.screen)
                eventString = event.toString()
            }

            is NavigationEvent.PopScreen -> {
                event.popScreen(backStack)
                eventString = event.toString()
            }

            is NavigationEvent.SetRootScreen -> {
                backStack.apply {
                    clear()
                    add(event.screen)
                }
                eventString = event.toString()
            }
        }

        val currentBackStack = backStack.stackString()
        Log.d(TAG, "$eventString$initialBackStack > $currentBackStack")
    }

    private fun NavigationEvent.PopScreen.popScreen(
        backStack: NavBackStack<NavKey>
    ) {
        screen?.run {
            // Pop until the specific screen, removing all screens above it.
            backStack
                .indexOfLast { it == this }
                .takeIf { it != -1 }
                ?.let { targetIndex ->
                    // Clears everything above the target index in one go
                    backStack.subList(targetIndex + 1, backStack.size).clear()
                }
        } ?: run {
            // Pop to the previous screen, but only if there is more than one screen in the stack.
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        }
    }

    sealed interface NavigationEvent {
        /**
         * Adds a new [screen] to the navigation stack.
         */
        data class PushScreen(val screen: Screen) : NavigationEvent {
            override fun toString(): String =
                "🔻Push to ${screen.javaClass.simpleName} :: "
        }

        /**
         * Returns to the previous screen.
         * If a [screen] is provided, it will pop until that specific screen.
         */
        data class PopScreen(val screen: Screen? = null) : NavigationEvent {
            override fun toString(): String =
                "🔺Pop${screen?.run { " to ${javaClass.simpleName}" }.orEmpty()} :: "
        }

        /**
         * Set specific screen as the root of the stack, clearing all previous screens.
         */
        data class SetRootScreen(val screen: Screen) : NavigationEvent {
            override fun toString(): String =
                "🔻Set root to ${screen.javaClass.simpleName} :: "
        }
    }

    private fun NavBackStack<NavKey>.stackString(): String =
        "[ ${joinToString { it.javaClass.simpleName }} ]"

    const val TAG = "NavigationHandler"
}