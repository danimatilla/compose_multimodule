package com.dxmxp.seed.screens.rockets

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.model.Rocket
import com.dxmxp.domain.use_case.GetRocketsUseCase
import com.dxmxp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RocketsViewModel @Inject constructor(
    private val getRocketsUseCase: GetRocketsUseCase
) : BaseViewModel<RocketsViewModel.State, RocketsViewModel.Effect, RocketsViewModel.Event>() {

    data class State(
        val isLoading: Boolean = false,
        val rockets: List<Rocket> = emptyList(),
        val error: String? = null
    )

    sealed interface Event {
        data object LoadRockets : Event
        data class OnRocketClicked(val rocket: Rocket) : Event
    }

    sealed interface Effect {
        data class NavigateToDetail(val rocketId: String) : Effect
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.LoadRockets -> fetchRockets()
            is Event.OnRocketClicked -> setEffect { Effect.NavigateToDetail(event.rocket.id) }
        }
    }

    init {
        setEvent(Event.LoadRockets)
    }

    private fun fetchRockets() {
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                getRocketsUseCase(Unit).collectLatest { rocketList ->
                    setState { 
                        copy(
                            isLoading = false, 
                            rockets = rocketList,
                            error = if (rocketList.isEmpty()) "No rockets found" else null
                        ) 
                    }
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.localizedMessage ?: "Unknown error") }
            }
        }
    }
}
