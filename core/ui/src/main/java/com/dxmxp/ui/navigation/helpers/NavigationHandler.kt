package com.dxmxp.ui.navigation.helpers

import android.util.Log
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.common.DataObserver
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.navigation.helpers.EventExtension.popScreen
import com.dxmxp.ui.navigation.helpers.EventExtension.pushScreen
import com.dxmxp.ui.navigation.helpers.EventExtension.setRootScreen

object NavigationHandler {

    suspend fun handleEvent(
        backStack: NavBackStack<NavKey>,
        event: NavigationEvent,
        dataObserver: DataObserver
    ) {
        var eventString: String
        val initialBackStack = backStack.stackString()

        when (event) {
            is NavigationEvent.PushScreen -> {
                event.pushScreen(backStack, dataObserver)
                eventString = event.toString()
            }

            is NavigationEvent.PopScreen -> {
                event.popScreen(backStack)
                eventString = event.toString()
            }

            is NavigationEvent.SetRootScreen -> {
                event.setRootScreen(backStack)
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
        data class PushScreen(val screen: Screen, val data: Any? = null) : NavigationEvent {
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

    private const val TAG = "NavigationHandler"
}