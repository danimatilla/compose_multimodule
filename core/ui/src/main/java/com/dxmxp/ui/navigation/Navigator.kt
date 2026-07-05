package com.dxmxp.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import com.dxmxp.ui.common.DataObserver
import kotlinx.coroutines.flow.Flow

interface Navigator {
    val events: Flow<NavigationOrchestrator.NavigationEvent>
    fun navigate(event: NavigationOrchestrator.NavigationEvent)

    fun push(screen: Screen, data: Any? = null) {
        navigate(NavigationOrchestrator.NavigationEvent.PushScreen(screen, data))
    }

    fun pop(screen: Screen? = null) {
        navigate(NavigationOrchestrator.NavigationEvent.PopScreen(screen))
    }

    fun setRoot(screen: Screen) {
        navigate(NavigationOrchestrator.NavigationEvent.SetRootScreen(screen))
    }
}

/**
 * An implementation of [Navigator] that delegates to [NavigationManager].
 * This bridges the gap between the UI-based Navigator and the ViewModel-based Manager.
 */
class NavigationManagerBridge(private val navigationManager: NavigationManager) : Navigator {
    override val events = navigationManager.events
    override fun navigate(event: NavigationOrchestrator.NavigationEvent) {
        navigationManager.navigate(event)
    }
}

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided")
}

val LocalDataObserver = staticCompositionLocalOf<DataObserver> {
    error("No DataObserver provided")
}

