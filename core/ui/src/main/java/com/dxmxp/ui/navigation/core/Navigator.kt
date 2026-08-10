package com.dxmxp.ui.navigation.core

import androidx.compose.runtime.staticCompositionLocalOf
import com.dxmxp.ui.navigation.model.Route
import com.dxmxp.ui.navigation.model.Screen
import kotlinx.coroutines.flow.Flow

interface Navigator {
    val events: Flow<NavigationEvent>
    fun navigate(event: NavigationEvent)

    fun push(route: Route) {
        navigate(NavigationEvent.PushScreen(route))
    }

    fun pop(route: Route? = null) {
        navigate(NavigationEvent.PopScreen(route))
    }

    fun setRoot(route: Route) {
        navigate(NavigationEvent.SetRootScreen(route))
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

