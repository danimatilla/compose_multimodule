package com.dxmxp.ui.navigation

import com.dxmxp.ui.navigation.helpers.NavigationHandler.NavigationEvent
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

    fun push(screen: Screen, data: Any? = null) {
        navigate(NavigationEvent.PushScreen(screen, data))
    }

    fun pop(screen: Screen? = null) {
        navigate(NavigationEvent.PopScreen(screen))
    }

    fun setRoot(screen: Screen) {
        navigate(NavigationEvent.SetRootScreen(screen))
    }
}
