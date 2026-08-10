package com.dxmxp.ui.navigation.core

import com.dxmxp.ui.navigation.model.Route
import com.dxmxp.ui.navigation.model.Screen
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton service that manages navigation events across the application.
 * This allows ViewModels and other non-UI components to trigger navigation
 * without being coupled to the Compose hierarchy.
 */
@Singleton
class NavigationManager @Inject constructor() {

    private val _events = MutableSharedFlow<NavigationEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    fun navigate(event: NavigationEvent) {
        _events.tryEmit(event)
    }

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
