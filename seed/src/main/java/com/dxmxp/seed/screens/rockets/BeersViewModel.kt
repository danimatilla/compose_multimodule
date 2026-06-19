package com.dxmxp.seed.screens.rockets

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.common.PaginationConfig.DEFAULT_PAGE_SIZE
import com.dxmxp.domain.model.Beer
import com.dxmxp.seed.use_case.GetBeersUseCase
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.common.launchResultFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BeersViewModel @Inject constructor(
    private val getBeersUseCase: GetBeersUseCase
) : BaseViewModel<BeersViewModel.State, BeersViewModel.Effect, BeersViewModel.Event>() {

    data class State(
        val beers: List<Beer> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: String? = null,
        val endReached: Boolean = false
    ) {
        val canLoadNextPage: Boolean get() = !isLoading && !endReached
    }

    sealed interface Event {
        data object LoadBeers : Event
        data object LoadNextPage : Event
        data object Refresh : Event
        data class OnBeerClicked(val beer: Beer) : Event
    }

    sealed interface Effect {
        data class NavigateToDetail(val rocketId: String) : Effect
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.LoadBeers -> fetchBeers(shouldReset = true)
            is Event.LoadNextPage -> {
                if (uiState.value.canLoadNextPage) {
                    fetchBeers(shouldReset = false)
                }
            }
            is Event.Refresh -> fetchBeers(shouldReset = true, isRefreshing = true)
            is Event.OnBeerClicked -> setEffect { Effect.NavigateToDetail(event.beer.id) }
        }
    }

    init {
        setEvent(Event.LoadBeers)
    }

    private fun fetchBeers(shouldReset: Boolean, isRefreshing: Boolean = false) {
        if (uiState.value.isLoading || uiState.value.isRefreshing) return

        viewModelScope.launchResultFlow(
            flow = getBeersUseCase(shouldReset),
            setState = { setState(it) },
            onLoading = { isLoading ->
                copy(
                    isLoading = isLoading && !isRefreshing,
                    isRefreshing = isLoading && isRefreshing
                )
            },
            onError = { copy(error = it.message, isLoading = false, isRefreshing = false) },
            onSuccess = { result ->
                val newBeers = result ?: emptyList()
                copy(
                    beers = if (shouldReset) newBeers else beers + newBeers,
                    error = if (shouldReset && newBeers.isEmpty()) "No beers found" else null,
                    endReached = newBeers.size < DEFAULT_PAGE_SIZE,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        )
    }
}
