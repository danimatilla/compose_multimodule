package com.dxmxp.seed.screens.rockets

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.use_case.GetBeersUseCase
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.common.launchResultFlow
import com.dxmxp.ui.common.mapListData
import com.dxmxp.ui.model.BeerUiModel
import com.dxmxp.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BeersViewModel @Inject constructor(
    private val getBeersUseCase: GetBeersUseCase
) : BaseViewModel<BeersViewModel.State, BeersViewModel.Effect, BeersViewModel.Event>() {

    data class State(
        val beers: List<BeerUiModel>? = null,
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: String? = null,
        val endReached: Boolean = false
    ) {
        val canLoadNextPage: Boolean get() = !isLoading && !endReached
        val launchIf: Boolean get() = !isLoading && !isRefreshing
    }

    sealed interface Event {
        data object LoadBeers : Event
        data object LoadNextPage : Event
        data object Refresh : Event
        data class OnBeerClicked(val beer: BeerUiModel) : Event
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
        viewModelScope.launchResultFlow(
            flow = getBeersUseCase(shouldReset).mapListData { it.toUiModel() },
            launchIf = uiState.value.launchIf,
            shouldReset = shouldReset,
            currentList = { beers },
            setState = ::setState,
            onLoading = { loading ->
                copy(
                    isLoading = loading && !isRefreshing,
                    isRefreshing = loading && isRefreshing
                )
            },
            onError = { copy(error = it.message, isLoading = false, isRefreshing = false) },
            onSuccess = { items, endReached ->
                copy(
                    beers = items,
                    endReached = endReached,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        )
    }
}
