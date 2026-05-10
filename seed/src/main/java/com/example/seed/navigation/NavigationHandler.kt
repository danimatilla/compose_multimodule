package com.example.seed.navigation

import android.util.Log
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.seed.navigation.screens.Screen

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
                val targetScreen = event.screen
                if (targetScreen == null) {
                    // Pop to the previous screen, but only if there is more than one screen in the stack.
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                } else {
                    // Pop until the specific screen, removing all screens above it.
                    val targetIndex = backStack.indexOfLast { it == targetScreen }
                    if (targetIndex != -1) {
                        // Clears everything above the target index in one go
                        backStack.subList(targetIndex + 1, backStack.size).clear()
                    }
                }
                eventString = event.toString()
            }
        }

        val currentBackStack = backStack.stackString()
        Log.d(TAG, "$eventString$initialBackStack > $currentBackStack")
    }

    sealed interface NavigationEvent {
        /**
         * Adds a new [screen] to the navigation stack.
         */
        data class PushScreen(val screen: Screen) : NavigationEvent {

            override fun toString(): String =
                "🔻 ${screen.javaClass.simpleName} :: "
        }

        /**
         * Returns to the previous screen.
         * If a [screen] is provided, it will pop until that specific screen.
         */
        data class PopScreen(val screen: Screen? = null) : NavigationEvent {

            override fun toString(): String =
                "🔺 ${screen?.run {"${javaClass.simpleName} :: "}.orEmpty()}"
        }
    }

    private fun NavBackStack<NavKey>.stackString(): String =
        "[ ${joinToString { it.javaClass.simpleName }} ]"

    const val TAG = "NavigationHandler"
}
