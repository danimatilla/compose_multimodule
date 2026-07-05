package com.dxmxp.ui.navigation.core

import androidx.compose.runtime.staticCompositionLocalOf
import com.dxmxp.ui.navigation.model.Screen
import kotlinx.coroutines.flow.Flow

interface Navigator {
    val events: Flow<NavigationEvent>
    fun navigate(event: NavigationEvent)

    fun push(screen: Screen) {
        navigate(NavigationEvent.PushScreen(screen))
    }

    fun pop(screen: Screen? = null) {
        navigate(NavigationEvent.PopScreen(screen))
    }

    fun setRoot(screen: Screen) {
        navigate(NavigationEvent.SetRootScreen(screen))
    }
}

/**
 * An implementation of [Navigator] that delegates to [NavigationManager].
 * This bridges the gap between the UI-based Navigator and the ViewModel-based Manager.
 */
class NavigationManagerBridge(private val navigationManager: NavigationManager) : Navigator {
    override val events = navigationManager.events
    override fun navigate(event: NavigationEvent) {
        navigationManager.navigate(event)
    }
}

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided")
}

