package com.dxmxp.seed.screens.login

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.AppException
import com.dxmxp.domain.common.fold
import com.dxmxp.domain.use_case.AutoLoginUseCase
import com.dxmxp.domain.use_case.LoginUseCase
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.common.collectInto
import com.dxmxp.ui.navigation.core.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val autoLoginUseCase: AutoLoginUseCase,
    private val navigationManager: NavigationManager
) : BaseViewModel<LoginViewModel.State, LoginViewModel.Effect, LoginViewModel.Event>() {

    data class State(
        val username: String = "emilys",
        val password: String = "emilyspass",
        val passwordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val isCheckingSession: Boolean = false
    )

    interface Event {
        data class OnUsernameChanged(val username: String) : Event
        data class OnPasswordChanged(val password: String) : Event
        data object OnLoginClick : Event
        data object OnTogglePasswordVisibility : Event
    }

    interface Effect {
        data class ShowError(val message: String) : Effect
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnUsernameChanged -> setState { copy(username = event.username) }
            is Event.OnPasswordChanged -> setState { copy(password = event.password) }
            is Event.OnLoginClick -> login()
            is Event.OnTogglePasswordVisibility -> setState { copy(passwordVisible = !passwordVisible) }
        }
    }

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.collectInto(
            flow = autoLoginUseCase(Unit),
            setState = ::setState
        ) { result ->
            result.fold(
                onLoading = { copy(isCheckingSession = it) },
                onSuccess = { _, _ ->
                    navigateToMain()
                    copy(isCheckingSession = false)
                },
                onError = { exception ->
                    if (exception !is AppException.UnauthorizedException) {
                        setEffect { Effect.ShowError(exception.message.orEmpty()) }
                    }
                    copy(isCheckingSession = false)
                }
            )
        }
    }

    private fun login() {
        val state = uiState.value
        val flow = loginUseCase(
            LoginUseCase.Input(
                username = state.username.orEmpty(),
                password = state.password.orEmpty()
            )
        )

        viewModelScope.collectInto(
            flow = flow,
            setState = ::setState
        ) { result ->
            result.fold(
                onLoading = { copy(isLoading = it) },
                onSuccess = { _, _ ->
                    navigateToMain()
                    copy(isLoading = false)
                },
                onError = { exception ->
                    setEffect { Effect.ShowError(exception.message.orEmpty()) }
                    copy(isLoading = false)
                }
            )
        }
    }

    private fun navigateToMain() {
        navigationManager.setRoot(MainScaffoldGraph)
    }
}
