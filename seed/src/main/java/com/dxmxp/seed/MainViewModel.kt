package com.dxmxp.seed

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.fold
import com.dxmxp.domain.use_case.AutoLoginUseCase
import com.dxmxp.domain.use_case.HasSessionUseCase
import com.dxmxp.navigation.core.RouteRegistry
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route
import com.dxmxp.navigation.utils.DeepLinkHandler
import com.dxmxp.seed.navigation.routes.AuthGraph
import com.dxmxp.seed.navigation.routes.MainGraph
import com.dxmxp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val hasSessionUseCase: HasSessionUseCase,
    private val autoLoginUseCase: AutoLoginUseCase,
    val routeRegistry: RouteRegistry,
    private val deepLinkHandler: DeepLinkHandler
) : BaseViewModel<MainViewModel.State, MainViewModel.Effect, MainViewModel.Event>() {

    data class State(
        val initialRoute: Route? = null,
        val showBottomBar: Boolean = false,
        val pendingRoute: Route? = null,
        val currentGraph: Graph? = null
    )

    interface Event {
        data class HandleDeepLink(val uri: Uri) : Event
        data class OnRouteChanged(val route: Route?) : Event
    }

    interface Effect {
        data class SetRoot(val route: Route) : Effect
        data class SetStack(val routes: List<Route>) : Effect
    }

    override fun createInitialState(): State = State()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            if (!hasSessionUseCase(Unit)) {
                updateInitialRoute(AuthGraph)
                return@launch
            }

            autoLoginUseCase(Unit).collect { result ->
                result.fold(
                    onLoading = {
                        setState { copy(initialRoute = null) }
                    },
                    onSuccess = { _, _ ->
                        updateInitialRoute(MainGraph.Home)
                    },
                    onError = { exception ->
                        if (exception is AppException.UnauthorizedException) {
                            updateInitialRoute(AuthGraph)
                        } else {
                            // Network error or other, we still have the token in memory
                            updateInitialRoute(MainGraph.Home)
                        }
                    }
                )
            }
        }
    }

    private fun updateInitialRoute(route: Route) {
        setState {
            copy(
                initialRoute = route,
                showBottomBar = shouldShowBottomBar(route)
            )
        }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.HandleDeepLink -> handleDeepLink(event.uri)
            is Event.OnRouteChanged -> onRouteChange(event)
        }
    }

    private fun onRouteChange(event: Event.OnRouteChanged) {
        val state = uiState.value
        val showBar = shouldShowBottomBar(event.route)

        event.route?.let {
            val graph = routeRegistry.getGraphForRoute(it)
            setState { copy(currentGraph = graph) }
        }

        // If we have a pending route and we just transitioned to an authenticated area
        if ((state.pendingRoute != null) && (event.route != null) && requiresAuth(event.route)) {
            val destination = state.pendingRoute
            setState { copy(pendingRoute = null, showBottomBar = showBar) }
            navigateToAuthenticatedRoute(destination)
        } else {
            setState { copy(showBottomBar = showBar) }
        }
    }

    private fun handleDeepLink(uri: Uri) {
        viewModelScope.launch {
            deepLinkHandler.handle(uri)?.let { route ->
                val hasSession = hasSessionUseCase(Unit)

                if (requiresAuth(route) && !hasSession) {
                    // Save for after login
                    setState { copy(pendingRoute = route) }
                    setEffect { Effect.SetRoot(AuthGraph) }
                } else {
                    navigateToAuthenticatedRoute(route)
                }
            }
        }
    }

    private fun navigateToAuthenticatedRoute(route: Route) {
        when (route) {
            AuthGraph -> setEffect { Effect.SetRoot(AuthGraph) }
            MainGraph.Home, MainGraph -> setEffect { Effect.SetRoot(MainGraph.Home) }
            else -> setEffect { Effect.SetStack(listOf(MainGraph.Home, route)) }
        }
    }

    /**
     * Determines if the BottomBar should be shown for a given route.
     * Inherits visibility from the parent Graph if not explicitly overridden by the route.
     */
    private fun shouldShowBottomBar(route: Route?): Boolean {
        if (route == null) return false

        val graph = routeRegistry.getGraphForRoute(route)

        // If the graph itself says no bar, we hide it for everything inside.
        if (graph != null && !graph.showMainBottomBar) return false

        // Otherwise, respect the route's own property.
        return route.showMainBottomBar
    }

    /**
     * Determines if a route requires authentication.
     * Inherits from the parent Graph if not explicitly overridden by the route.
     */
    private fun requiresAuth(route: Route?): Boolean {
        if (route == null) return true

        val graph = routeRegistry.getGraphForRoute(route)

        // If the graph itself is public, all screens inside are public unless they override it.
        if (graph != null && !graph.requiresAuth) return false

        return route.requiresAuth
    }
}
