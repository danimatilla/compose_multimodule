package com.dxmxp.seed

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.fold
import com.dxmxp.domain.use_case.AutoLoginUseCase
import com.dxmxp.domain.use_case.HasSessionUseCase
import com.dxmxp.navigation.core.NavAction
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
    private val routeRegistry: RouteRegistry,
    private val deepLinkHandler: DeepLinkHandler,
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
        data class Navigate(val action: NavAction) : Effect
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
                showBottomBar = routeRegistry.shouldShowBottomBar(route)
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
        val showBar = routeRegistry.shouldShowBottomBar(event.route)

        event.route?.let {
            val graph = routeRegistry.getGraphForRoute(it)
            setState { copy(currentGraph = graph) }
        }

        // If we have a pending route and we just transitioned to an authenticated area
        if ((state.pendingRoute != null) && (event.route != null) && routeRegistry.requiresAuth(event.route)) {
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

                if (routeRegistry.requiresAuth(route) && !hasSession) {
                    // Save for after login
                    setState { copy(pendingRoute = route) }
                    setEffect { Effect.Navigate(NavAction.Root(AuthGraph)) }
                } else {
                    navigateToAuthenticatedRoute(route)
                }
            }
        }
    }

    private fun navigateToAuthenticatedRoute(route: Route) {
        val graph = routeRegistry.getGraphForRoute(route)
        val isModal = routeRegistry.isModal(route)

        setEffect {
            Effect.Navigate(
                NavAction.UpdateStack {
                    graph?.let { graph ->
                        if (graph is MainGraph) {
                            if (isModal) push(route)
                            else root(route)
                        } else {
                            val graph = if (route != graph) graph.withInitialRoute(route) else graph
                            if (isModal) {
                                root(MainGraph)
                                push(graph)
                            } else {
                                root(graph)
                            }
                        }
                    }
                }
            )
        }
    }
}
