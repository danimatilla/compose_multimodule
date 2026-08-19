package com.dxmxp.ui.navigation.scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.State
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.dxmxp.ui.navigation.core.NavigationEvent
import com.dxmxp.ui.navigation.model.Graph
import com.dxmxp.ui.navigation.model.Route
import com.dxmxp.ui.navigation.model.Screen
import com.dxmxp.ui.navigation.orchestration.NavigationOrchestrator

/**
 * Generic controller for scaffold-level navigation.
 * Reduces boilerplate in MainScaffold and StoriesScaffold by automatically handling
 * navigation events and backstack management.
 */
@Composable
fun rememberScaffoldController(
    initialRoute: Route,
    graph: Graph,
    orchestrator: NavigationOrchestrator
): ScaffoldController {
    val backStack = rememberNavBackStack(initialRoute)
    
    return remember(backStack, graph, orchestrator) {
        ScaffoldController(
            backStack = backStack,
            graph = graph,
            orchestrator = orchestrator
        )
    }.also { controller ->
        // Automatically observe and process navigation events for this scaffold
        LaunchedEffect(backStack, graph, orchestrator) {
            orchestrator.events.collect { event ->
                controller.handleNavigationEvent(event)
            }
        }
    }
}

/**
 * Controls navigation within a scaffold (MainScaffold, StoriesScaffold, etc).
 * Automatically handles event propagation and backstack management.
 */
class ScaffoldController(
    val backStack: NavBackStack<NavKey>,
    val graph: Graph,
    private val orchestrator: NavigationOrchestrator
) {
    private val _currentDestination = mutableStateOf<NavKey?>(backStack.lastOrNull())
    val currentDestinationState: State<NavKey?> = _currentDestination

    val currentDestination: NavKey?
        get() = currentDestinationState.value

    val isAtRoot: Boolean
        get() = backStack.size <= 1

    suspend fun handleNavigationEvent(event: NavigationEvent) {
        val handled = orchestrator.handleEventForBackstack(backStack, event, containerGraph = graph)
        if (handled) {
            _currentDestination.value = backStack.lastOrNull()
        }
    }

    /**
     * Check if a route is the current destination.
     */
    fun isCurrentDestination(route: Route): Boolean {
        return currentDestination == route
    }

    /**
     * Get all routes in the current back stack.
     */
    fun getBackStack(): List<NavKey> = backStack.toList()

    /**
     * Clear and reset to initial route.
     */
    fun reset(initialRoute: Route) {
        backStack.clear()
        backStack.add(initialRoute)
        _currentDestination.value = initialRoute
    }
}

