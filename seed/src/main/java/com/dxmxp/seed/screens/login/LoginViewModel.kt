package com.dxmxp.seed.screens.login

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.use_case.AutoLoginUseCase
import com.dxmxp.domain.use_case.LoginUseCase
import com.dxmxp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val autoLoginUseCase: AutoLoginUseCase
) : BaseViewModel<LoginViewModel.State, LoginViewModel.Effect, LoginViewModel.Event>() {

    data class State(
        val username: String? = null,
        val password: String? = null,
        val isLoading: Boolean? = null
    )

    interface Event {
        data class OnUsernameChanged(val username: String) : Event
        data class OnPasswordChanged(val password: String) : Event
        data object OnLoginClick : Event
    }

    interface Effect {
        data object NavigateToMain : Effect
        data class ShowError(val message: String) : Effect
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnUsernameChanged -> setState { copy(username = event.username) }
            is Event.OnPasswordChanged -> setState { copy(password = event.password) }
            Event.OnLoginClick -> login()
        }
    }

    init {
        setState { copy(username = "", password = "", isLoading = true) }
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            autoLoginUseCase(Unit).collect { result ->
                when (result) {
                    is DataResult.Loading -> {
                        setState { copy(isLoading = true, username = "", password = "") }
                    }
                    is DataResult.Success -> {
                        setState { copy(isLoading = false) }
                        setEffect { Effect.NavigateToMain }
                    }
                    is DataResult.Error -> {
                        setState { copy(isLoading = false, username = "emilys", password = "emilyspass") }
                    }
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            val currentState = uiState.value
            loginUseCase(
                LoginUseCase.Input(
                    username = currentState.username ?: "",
                    password = currentState.password ?: ""
                )
            ).collect { result ->
                when (result) {
                    is DataResult.Loading -> setState { copy(isLoading = true) }
                    is DataResult.Success -> {
                        setState { copy(isLoading = false) }
                        setEffect { Effect.NavigateToMain }
                    }
                    is DataResult.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect { Effect.ShowError(result.exception.message ?: "Unknown Error") }
                    }
                }
            }
        }
    }
}
