package com.dxmxp.seed

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.fold
import com.dxmxp.domain.use_case.AutoLoginUseCase
import com.dxmxp.domain.use_case.HasSessionUseCase
import com.dxmxp.navigation.core.RouteRegistry
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
    private val routeRegistry: RouteRegistry,
    private val deepLinkHandler: DeepLinkHandler
) : BaseViewModel<MainViewModel.State, MainViewModel.Effect, MainViewModel.Event>() {

    data class State(
        val initialRoute: Route? = null,
        val showBottomBar: Boolean = false,
        val pendingRoute: Route? = null
    )

    interface Event {
        data class HandleDeepLink(val uri: Uri) : Event
        data class OnRouteChanged(val route: Route?) : Event
    }

    interface Effect {
        data class SetRoot(val route: Route) : Effect
    }

    override fun createInitialState(): State = State()

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
            is Event.OnRouteChanged -> {
                val state = uiState.value
                val showBar = shouldShowBottomBar(event.route)
                
                // If we have a pending route and we just transitioned to an authenticated area (MainGraph)
                if (state.pendingRoute != null && (event.route is MainGraph)) {
                    val destination = state.pendingRoute
                    setState { copy(pendingRoute = null, showBottomBar = showBar) }
                    setEffect { Effect.SetRoot(destination) }
                } else {
                    setState { copy(showBottomBar = showBar) }
                }
            }
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
                    setEffect { Effect.SetRoot(route) }
                }
            }
        }
    }
}
