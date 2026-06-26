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
) : BaseViewModel<LoginContract.State, LoginContract.Effect, LoginContract.Event>() {

    init {
        checkSession()
    }

    override fun createInitialState(): LoginContract.State = LoginContract.State(
        username = "",
        password = "",
        isLoading = true // Start loading for auto-login check
    )

    override fun handleEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.OnUsernameChanged -> setState { copy(username = event.username) }
            is LoginContract.Event.OnPasswordChanged -> setState { copy(password = event.password) }
            LoginContract.Event.OnLoginClick -> login()
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            autoLoginUseCase(Unit).collect { result ->
                when (result) {
                    is DataResult.Loading -> {
                        // Ensure we stay in loading state with empty fields during auto-login
                        setState { copy(isLoading = true, username = "", password = "") }
                    }
                    is DataResult.Success -> {
                        setState { copy(isLoading = false) }
                        setEffect { LoginContract.Effect.NavigateToMain }
                    }
                    is DataResult.Error -> {
                        setState { copy(isLoading = false, username = "emilys", password = "emilyspass") }
                        // Silent error, stay on login
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
                    username = currentState.username,
                    password = currentState.password
                )
            ).collect { result ->
                when (result) {
                    is DataResult.Loading -> setState { copy(isLoading = true) }
                    is DataResult.Success -> {
                        setState { copy(isLoading = false) }
                        setEffect { LoginContract.Effect.NavigateToMain }
                    }
                    is DataResult.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect { LoginContract.Effect.ShowError(result.exception.message ?: "Unknown Error") }
                    }
                }
            }
        }
    }
}
