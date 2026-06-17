package com.dxmxp.seed.screens.rockets

import com.dxmxp.domain.model.Beer
import com.dxmxp.seed.use_case.GetBeersUseCase
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.common.launchResultFlow
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BeersViewModel @Inject constructor(
    private val getBeersUseCase: GetBeersUseCase
) : BaseViewModel<BeersViewModel.State, BeersViewModel.Effect, BeersViewModel.Event>() {

    data class State(
        val beers: List<Beer> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val endReached: Boolean = false
    )

    sealed interface Event {
        data object LoadBeers : Event
        data object LoadNextPage : Event
        data class OnBeerClicked(val beer: Beer) : Event
    }

    sealed interface Effect {
        data class NavigateToDetail(val rocketId: String) : Effect
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.LoadBeers -> fetchBeers(isNextPage = false)
            is Event.LoadNextPage -> fetchBeers(isNextPage = true)
            is Event.OnBeerClicked -> setEffect { Effect.NavigateToDetail(event.beer.id) }
        }
    }

    init {
        setEvent(Event.LoadBeers)
    }

    private fun fetchBeers(isNextPage: Boolean) {
        if (isNextPage && (uiState.value.isLoading || uiState.value.endReached)) return

        viewModelScope.launchResultFlow(
            flow = getBeersUseCase(isNextPage),
            setState = { setState(it) },
            onLoading = { copy(isLoading = it) },
            onError = { copy(error = it.message) },
            onSuccess = { result ->
                val newBeers = result ?: emptyList()
                copy(
                    beers = if (isNextPage) beers + newBeers else newBeers,
                    error = if (!isNextPage && newBeers.isEmpty()) "No beers found" else null,
                    endReached = newBeers.isEmpty()
                )
            }
        )
    }
}
