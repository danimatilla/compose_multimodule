package com.dxmxp.seed

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.fold
import com.dxmxp.domain.use_case.AutoLoginUseCase
import com.dxmxp.domain.use_case.HasSessionUseCase
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
    private val deepLinkHandler: DeepLinkHandler
) : BaseViewModel<MainViewModel.State, MainViewModel.Effect, MainViewModel.Event>() {

    data class State(
        val initialRoute: Route? = null
    )

    interface Event {
        data class HandleDeepLink(val uri: Uri) : Event
    }

    interface Effect {
        data class NavigateTo(val route: Route) : Effect
    }

    override fun createInitialState(): State = State()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            if (!hasSessionUseCase(Unit)) {
                setState { copy(initialRoute = AuthGraph) }
                return@launch
            }

            autoLoginUseCase(Unit).collect { result ->
                result.fold(
                    onLoading = {
                        setState { copy(initialRoute = null) }
                    },
                    onSuccess = { _, _ ->
                        setState { copy(initialRoute = MainGraph) }
                    },
                    onError = { exception ->
                        if (exception is AppException.UnauthorizedException) {
                            setState { copy(initialRoute = AuthGraph) }
                        } else {
                            // Network error or other, we still have the token in memory
                            setState { copy(initialRoute = MainGraph) }
                        }
                    }
                )
            }
        }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.HandleDeepLink -> handleDeepLink(event.uri)
        }
    }

    private fun handleDeepLink(uri: Uri) {
        deepLinkHandler.handle(uri)?.let { route ->
            setEffect { Effect.NavigateTo(route) }
        }
    }
}
