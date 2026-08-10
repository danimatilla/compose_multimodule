package com.dxmxp.ui.navigation.orchestration

import android.util.Log
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.core.NavigationEvent
import com.dxmxp.ui.navigation.core.NavigationManager
import com.dxmxp.ui.navigation.core.RouteRegistry
import com.dxmxp.ui.navigation.model.Graph
import com.dxmxp.ui.navigation.model.Route
import com.dxmxp.ui.navigation.model.Screen
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
    private val routeRegistry: RouteRegistry
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
        val route = event.routeOrNull()

        // Determine if this event applies to this backstack
        val appliesHere = when {
            targetGraph == null -> true // Root level, handle everything not in subgraphs
            route == null -> true // Pop without target always applies
            targetGraph.contains(route) -> true // Screen is in this graph
            else -> false
        }

        if (!appliesHere) {
            Log.d(TAG, "Event doesn't apply to graph ${targetGraph?.route}")
            return false
        }

        // Prevent navigation to modal screens from non-modal contexts (delegate to parent)
        if (targetGraph != null && 
            !targetGraph.isModal && 
            route != null && 
            routeRegistry.getAllGraphs()
                .filter { it.isModal }
                .any { it.contains(route) }
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

        event.handle(backStack)

        Log.d(TAG, "${event.logString()} $initialBackStack > ${backStack.stackString()}")
    }

    /**
     * Navigates from a ViewModel or non-UI component.
     */
    fun navigate(route: Route) {
        navigationManager.push(route)
    }

    fun popTo(route: Route) {
        navigationManager.pop(route)
    }

    fun pop() {
        navigationManager.pop()
    }

    fun setRoot(route: Route) {
        navigationManager.setRoot(route)
    }

    private fun NavigationEvent.logString() = when (this) {
        is NavigationEvent.PushScreen -> "🔻Push to ${route.javaClass.simpleName}"
        is NavigationEvent.PopScreen -> "🔺Pop${route?.run { " to ${javaClass.simpleName}" }.orEmpty()}"
        is NavigationEvent.SetRootScreen -> "🔻Set root to ${route.javaClass.simpleName}"
    }

    private fun NavBackStack<NavKey>.stackString(): String =
        "[ ${joinToString { it.javaClass.simpleName }} ]"

    private companion object {
        const val TAG = "NavigationOrchestrator"
    }
}

