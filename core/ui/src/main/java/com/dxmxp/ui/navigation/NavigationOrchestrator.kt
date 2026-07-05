package com.dxmxp.ui.navigation

import android.util.Log
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.common.DataObserver
import com.dxmxp.ui.navigation.helpers.EventExtension.popScreen
import com.dxmxp.ui.navigation.helpers.EventExtension.pushScreen
import com.dxmxp.ui.navigation.helpers.EventExtension.setRootScreen
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates navigation across the entire application.
 * Unifies event handling for both root and nested scaffolds.
 */
@Singleton
class NavigationOrchestrator @Inject constructor(
    private val navigationManager: NavigationManager,
    private val routeRegistry: RouteRegistry,
    private val dataObserver: DataObserver
) {
    
    val events: Flow<NavigationEvent> = navigationManager.events

    /**
     * Processes a navigation event for a specific backstack.
     * Returns true if the event was handled, false if it should be delegated.
     */
    suspend fun handleEventForBackstack(
        backStack: NavBackStack<NavKey>,
        event: NavigationEvent,
        targetGraph: Graph? = null
    ): Boolean {
        val screen = event.screenOrNull()

        // Determine if this event applies to this backstack
        val appliesHere = when {
            targetGraph == null -> true // Root level, handle everything not in subgraphs
            screen == null -> true // Pop without target always applies
            targetGraph.contains(screen) -> true // Screen is in this graph
            else -> false
        }

        if (!appliesHere) {
            Log.d(TAG, "Event doesn't apply to graph ${targetGraph?.route}")
            return false
        }

        // Prevent navigation to modal screens from non-modal contexts (delegate to parent)
        if (targetGraph != null && 
            !targetGraph.isModal && 
            screen != null && 
            routeRegistry.getAllGraphs()
                .filter { it.isModal }
                .any { it.contains(screen) }
        ) {
            Log.d(TAG, "Event is for modal, delegating to parent")
            return false
        }

        handleEvent(backStack, event)
        return true
    }

    /**
     * Directly handles a navigation event on a backstack.
     */
    private suspend fun handleEvent(
        backStack: NavBackStack<NavKey>,
        event: NavigationEvent
    ) {
        val initialBackStack = backStack.stackString()

        when (event) {
            is NavigationEvent.PushScreen -> event.pushScreen(backStack, dataObserver)
            is NavigationEvent.PopScreen -> event.popScreen(backStack)
            is NavigationEvent.SetRootScreen -> event.setRootScreen(backStack)
        }

        Log.d(TAG, "${event.logString()} $initialBackStack > ${backStack.stackString()}")
    }

    /**
     * Navigates from a ViewModel or non-UI component.
     */
    fun navigate(screen: Screen, data: Any? = null) {
        navigationManager.push(screen, data)
    }

    fun popTo(screen: Screen) {
        navigationManager.pop(screen)
    }

    fun pop() {
        navigationManager.pop()
    }

    fun setRoot(screen: Screen) {
        navigationManager.setRoot(screen)
    }

    private fun NavigationEvent.logString() = when (this) {
        is NavigationEvent.PushScreen -> "🔻Push to ${screen.javaClass.simpleName}"
        is NavigationEvent.PopScreen -> "🔺Pop${screen?.run { " to ${javaClass.simpleName}" }.orEmpty()}"
        is NavigationEvent.SetRootScreen -> "🔻Set root to ${screen.javaClass.simpleName}"
    }

    private fun NavBackStack<NavKey>.stackString(): String =
        "[ ${joinToString { it.javaClass.simpleName }} ]"

    sealed interface NavigationEvent {
        data class PushScreen(val screen: Screen, val data: Any? = null) : NavigationEvent
        data class PopScreen(val screen: Screen? = null) : NavigationEvent
        data class SetRootScreen(val screen: Screen) : NavigationEvent

        fun screenOrNull(): Screen? = when (this) {
            is PushScreen -> screen
            is PopScreen -> screen
            is SetRootScreen -> screen
        }
    }

    private companion object {
        const val TAG = "NavigationOrchestrator"
    }
}

