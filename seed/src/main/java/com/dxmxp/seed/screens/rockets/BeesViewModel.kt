package com.dxmxp.seed.screens.rockets

import com.dxmxp.domain.model.Beer
import com.dxmxp.seed.use_case.GetBeersUseCase
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.common.launchFlow
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BeesViewModel @Inject constructor(
    private val getBeersUseCase: GetBeersUseCase
) : BaseViewModel<BeesViewModel.State, BeesViewModel.Effect, BeesViewModel.Event>() {

    data class State(
        val isLoading: Boolean = false,
        val beers: List<Beer> = emptyList(),
        val error: String? = null
    )

    sealed interface Event {
        data object LoadBeers : Event
        data class OnBeerClicked(val beer: Beer) : Event
    }

    sealed interface Effect {
        data class NavigateToDetail(val rocketId: String) : Effect
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.LoadBeers -> fetchBeers()
            is Event.OnBeerClicked -> setEffect { Effect.NavigateToDetail(event.beer.id) }
        }
    }

    init {
        setEvent(Event.LoadBeers)
    }

    private fun fetchBeers() {
        viewModelScope.launchFlow(
            flow = getBeersUseCase(Unit),
            setState = { setState(it) },
            onLoading = { copy(isLoading = it) },
            onError = { copy(error = it) },
            onSuccess = { result ->
                copy(
                    beers = result,
                    error = if (result.isEmpty()) "No rockets found" else null
                )
            }
        )
    }
}
