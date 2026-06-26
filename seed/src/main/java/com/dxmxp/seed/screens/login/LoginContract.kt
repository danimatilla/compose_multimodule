package com.dxmxp.seed.screens.login

interface LoginContract {
    sealed interface Event {
        data class OnUsernameChanged(val username: String) : Event
        data class OnPasswordChanged(val password: String) : Event
        data object OnLoginClick : Event
    }

    data class State(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false
    )

    sealed interface Effect {
        data object NavigateToMain : Effect
        data class ShowError(val message: String) : Effect
    }
}
