package com.dxmxp.seed.screens.profile

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.use_case.LogoutUseCase
import com.dxmxp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<ProfileViewModel.State, ProfileViewModel.Effect, ProfileViewModel.Event>() {

    data class State(
        val isLoading: Boolean? = false
    )

    interface Event {
        data object OnLogoutClick : Event
    }

    interface Effect {
        data object NavigateToAuth : Effect
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnLogoutClick -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            logoutUseCase()
            setState { copy(isLoading = false) }
            setEffect { Effect.NavigateToAuth }
        }
    }
}
