package com.dxmxp.ui.navigation.helpers

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.common.DataObserver
import com.dxmxp.ui.navigation.helpers.NavigationHandler.NavigationEvent

internal object EventExtension {

    fun NavigationEvent.SetRootScreen.setRootScreen(
        backStack: NavBackStack<NavKey>,
    ) {
        backStack.run {
            clear()
            add(screen)
        }
    }

    suspend fun NavigationEvent.PushScreen.pushScreen(
        backStack: NavBackStack<NavKey>,
        dataObserver: DataObserver?,
    ) {
        backStack.run {
            if (lastOrNull() != screen) {
                add(screen)
            }
        }
        data?.let { dataObserver?.emit(it) }
    }


    fun NavigationEvent.PopScreen.popScreen(
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
}
