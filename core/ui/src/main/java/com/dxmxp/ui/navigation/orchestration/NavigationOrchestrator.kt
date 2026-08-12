package com.dxmxp.ui.navigation.orchestration

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.domain.base.Logger
import com.dxmxp.domain.repository.AuthRepository
import com.dxmxp.ui.navigation.core.NavigationEvent
import com.dxmxp.ui.navigation.core.NavigationManager
import com.dxmxp.ui.navigation.core.RouteRegistry
import com.dxmxp.ui.navigation.model.Graph
import com.dxmxp.ui.navigation.model.Route
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
            logger.d(TAG, "[${targetGraph?.route ?: "ROOT"}] ⏭️ Ignored: ${event.logString()} doesn't apply here")
            return false
        }

        // If user is not logged in and tries to access a protected route (non-auth), redirect to Login
        if (route != null) {
            val authGraph = routeRegistry.getAllGraphs().firstOrNull { it.route == "/auth" }
            val isAuthRoute = authGraph?.contains(route) ?: false

            if (!isAuthRoute && authRepository.getAccessToken() == null) {
                logger.d(TAG, "🛡️ Protected route access attempt without session. Redirecting to Login.")
                authGraph?.let {
                    handleEvent(backStack, NavigationEvent.SetRootScreen(it), targetGraph)
                }
                return true
            }
        }

        // Prevent navigation to modal screens from non-modal contexts (delegate to parent)
        if (targetGraph != null && 
            !targetGraph.isModal && 
            route != null && 
            routeRegistry.getAllGraphs()
                .filter { it.isModal }
                .any { it.contains(route) }
        ) {
            logger.d(TAG, "[${targetGraph.route}] ⤴️ Delegating: ${event.logString()} is MODAL")
            return false
        }

        handleEvent(backStack, event, targetGraph)
        return true
    }

    /**
     * Directly handles a navigation event on a backstack.
     */
    private suspend fun handleEvent(
        backStack: NavBackStack<NavKey>,
        event: NavigationEvent,
        targetGraph: Graph?
    ) {
        val graphTag = "[${targetGraph?.route ?: "ROOT"}]"
        val initialStack = backStack.stackString()

        event.handle(backStack)

        logger.d(TAG, "$graphTag ${event.logString()} | $initialStack ➔ ${backStack.stackString()}")
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

    private fun NavigationEvent.logString(): String {
        val route = routeOrNull()
        val routeName = route?.route ?: route?.javaClass?.simpleName ?: ""
        return when (this) {
            is NavigationEvent.PushScreen -> "PUSH($routeName)"
            is NavigationEvent.PopScreen -> "POP" + (route?.let { "($routeName)" } ?: "")
            is NavigationEvent.SetRootScreen -> "SET_ROOT($routeName)"
        }
    }

    private fun NavBackStack<NavKey>.stackString(): String =
        "[ ${joinToString(" > ") { (it as? Route)?.route ?: it.javaClass.simpleName }} ]"

    private companion object {
        const val TAG = "NavigationOrchestrator"
    }
}
