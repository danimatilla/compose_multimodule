package com.dxmxp.ui.navigation.orchestration

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.domain.base.Logger
import com.dxmxp.domain.repository.AuthRepository
import com.dxmxp.ui.navigation.core.NavigationEvent
import com.dxmxp.ui.navigation.core.NavigationManager
import com.dxmxp.ui.navigation.core.RouteRegistry
import com.dxmxp.ui.navigation.model.Graph
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
    private val authRepository: AuthRepository,
    private val logger: Logger
) {
    
    val events: Flow<NavigationEvent> = navigationManager.events

    /**
     * Processes a navigation event for a specific backstack.
     * Returns true if the event was handled, false if it should be delegated.
     */
    suspend fun handleEventForBackstack(
        backStack: NavBackStack<NavKey>,
        event: NavigationEvent,
        containerGraph: Graph? = null
    ): Boolean {
        val route = event.routeOrNull()

        // 1. Access Control: Only handled at ROOT level to prevent redirection loops.
        if (containerGraph == null && (route?.requiresAuth == true) && (authRepository.getAccessToken() == null)) {
            logger.d(TAG, "🛡️ Access Denied to ${route.route}. Redirecting to Auth.")
            routeRegistry.getAllGraphs().firstOrNull { it.route == "/auth" }?.let { 
                navigationManager.setRoot(it) 
            }
            return true
        }

        // 2. Hierarchical Decision: Determine which collector should handle this event.
        val appliesHere = if (containerGraph == null) {
            // ROOT level handles: 
            // - Any Graph (to switch scaffolds or open modals)
            // - Any route NOT contained in the currently active nested Graph
            // - Pop events (when they reach the root)
            val currentNested = backStack.lastOrNull() as? Graph
            when {
                route == null -> true
                route is Graph -> true
                currentNested == null -> true
                else -> !currentNested.contains(route)
            }
        } else {
            // SCAFFOLD level handles:
            // - Any route that is strictly INSIDE its graph (recursively)
            // - BUT NOT the graph itself (as it represents the scaffold container, handled by parent)
            route != null && containerGraph.contains(route) && route.route != containerGraph.route
        }

        if (!appliesHere) return false

        // 3. Auto-open Scaffolds: If a screen belongs to a different scaffold, Root should open it first.
        if (containerGraph == null && route != null && route !is Graph && event is NavigationEvent.PushScreen) {
            val hostGraph = routeRegistry.getAllGraphs().firstOrNull { it.contains(route) }
            // Only auto-open if the host is a different graph and not already at the top of the backstack.
            if (hostGraph != null && hostGraph.route != route.route && !backStack.contains(hostGraph)) {
                logger.d(TAG, "🔄 Root auto-opening host graph ${hostGraph.route} for ${route.route}")
                navigationManager.push(hostGraph)
                return true
            }
        }

        // 4. Final execution on the target backstack.
        logger.d(TAG, "[${containerGraph?.route ?: "ROOT"}] Handling: ${event.logString()}")
        event.handle(backStack)
        return true
    }

    fun hasActiveSession(): Boolean = authRepository.getAccessToken() != null

    private companion object {
        const val TAG = "NavigationOrchestrator"
    }
}
