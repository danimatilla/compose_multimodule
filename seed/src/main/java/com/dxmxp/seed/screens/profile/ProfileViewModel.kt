package com.dxmxp.seed.screens.profile

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.use_case.LogoutUseCase
import com.dxmxp.seed.navigation.routes.AuthGraph
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.navigation.core.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val navigationManager: NavigationManager
) : BaseViewModel<ProfileViewModel.State, ProfileViewModel.Effect, ProfileViewModel.Event>() {

    data class State(
        val isLoading: Boolean? = false
    )

    interface Event {
        data object OnLogoutClick : Event
    }

    interface Effect {
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
            navigationManager.setRoot(AuthGraph)
        }
    }
}
