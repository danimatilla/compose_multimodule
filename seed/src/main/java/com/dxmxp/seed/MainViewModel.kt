package com.dxmxp.seed

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.AppException
import com.dxmxp.domain.base.Logger
import com.dxmxp.domain.common.fold
import com.dxmxp.domain.use_case.AutoLoginUseCase
import com.dxmxp.domain.use_case.HasSessionUseCase
import com.dxmxp.navigation.core.NavAction
import com.dxmxp.navigation.core.RouteRegistry
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route
import com.dxmxp.navigation.utils.DeepLinkResult
import com.dxmxp.navigation.utils.DeepLinkRouter
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
    private val deepLinkRouter: DeepLinkRouter,
    private val logger: Logger
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
        data class OnRouteClicked(val route: Route) : Event
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
            is Event.OnRouteClicked -> navigateToAuthenticatedRoute(event.route)
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
            val isAuthenticated = hasSessionUseCase(Unit)
            when (val result = deepLinkRouter.handle(uri, mainGraph = MainGraph, authGraph = AuthGraph, isAuthenticated = isAuthenticated)) {
                is DeepLinkResult.Success -> {
                    setEffect { Effect.Navigate(result.action) }
                }
                is DeepLinkResult.RequiresAuth -> {
                    setState { copy(pendingRoute = result.pendingRoute) }
                    result.action?.let { action ->
                        setEffect { Effect.Navigate(action) }
                    }
                }
                is DeepLinkResult.Unresolved -> {
                    logger.e(TAG, result.toString())
                }
            }
        }
    }

    private fun navigateToAuthenticatedRoute(route: Route) {
        val action = deepLinkRouter.process(route, MainGraph)
        setEffect { Effect.Navigate(action) }
    }

    private companion object{
        val TAG: String = MainViewModel::class.java.name
    }
}
