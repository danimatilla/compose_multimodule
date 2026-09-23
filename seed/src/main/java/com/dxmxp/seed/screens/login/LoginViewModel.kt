package com.dxmxp.seed.screens.login

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.common.fold
import com.dxmxp.domain.use_case.LoginUseCase
import com.dxmxp.navigation.core.NavAction
import com.dxmxp.seed.navigation.routes.MainGraph
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.common.collectInto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginViewModel.State, LoginViewModel.Effect, LoginViewModel.Event>() {

    data class State(
        val username: String = "emilys",
        val password: String = "emilyspass",
        val passwordVisible: Boolean = false,
        val isLoading: Boolean = false
    )

    interface Event {
        data class OnUsernameChanged(val username: String) : Event
        data class OnPasswordChanged(val password: String) : Event
        data object OnLoginClick : Event
        data object OnTogglePasswordVisibility : Event
    }

    interface Effect {
        data class ShowError(val message: String) : Effect
        data class Navigate(val action: NavAction) : Effect
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
                    setEffect { Effect.Navigate(NavAction.Root(MainGraph)) }
                    copy(isLoading = false)
                },
                onError = { exception ->
                    setEffect { Effect.ShowError(exception.message.orEmpty()) }
                    copy(isLoading = false)
                }
            )
        }
    }
}
