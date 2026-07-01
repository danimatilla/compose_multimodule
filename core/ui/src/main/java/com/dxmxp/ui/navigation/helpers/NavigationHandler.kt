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
        dataObserver: DataObserver? = null
    ) {
        val initialBackStack = backStack.stackString()

        when (event) {
            is NavigationEvent.PushScreen -> event.pushScreen(backStack, dataObserver)
            is NavigationEvent.PopScreen -> event.popScreen(backStack)
            is NavigationEvent.SetRootScreen -> event.setRootScreen(backStack)
        }

        Log.d(TAG, "${event.logString()} $initialBackStack > ${backStack.stackString()}")
    }

    private fun NavigationEvent.logString() = when (this) {
        is NavigationEvent.PushScreen -> "🔻Push to ${screen.javaClass.simpleName}"
        is NavigationEvent.PopScreen -> "🔺Pop${screen?.run { " to ${javaClass.simpleName}" }.orEmpty()}"
        is NavigationEvent.SetRootScreen -> "🔻Set root to ${screen.javaClass.simpleName}"
    }

    sealed interface NavigationEvent {
        data class PushScreen(val screen: Screen, val data: Any? = null) : NavigationEvent

        data class PopScreen(val screen: Screen? = null) : NavigationEvent

        data class SetRootScreen(val screen: Screen) : NavigationEvent
    }

    private fun NavBackStack<NavKey>.stackString(): String =
        "[ ${joinToString { it.javaClass.simpleName }} ]"

    private const val TAG = "NavigationHandler"
}