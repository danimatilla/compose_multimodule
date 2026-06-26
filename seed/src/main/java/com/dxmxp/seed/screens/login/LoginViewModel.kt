package com.dxmxp.seed.screens.login

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.use_case.LoginUseCase
import com.dxmxp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginContract.State, LoginContract.Effect, LoginContract.Event>() {

    override fun createInitialState(): LoginContract.State = LoginContract.State(
        username = "emilys",
        password = "emilyspass"
    )

    override fun handleEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.OnUsernameChanged -> setState { copy(username = event.username) }
            is LoginContract.Event.OnPasswordChanged -> setState { copy(password = event.password) }
            LoginContract.Event.OnLoginClick -> login()
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
