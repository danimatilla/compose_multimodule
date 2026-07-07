package com.dxmxp.seed.screens.beers

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.use_case.GetBeersUseCase
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.common.collectPagingInto
import com.dxmxp.ui.common.mapData
import com.dxmxp.ui.mapper.BeerUiMapper
import com.dxmxp.ui.model.BeerUiModel
import com.dxmxp.ui.navigation.core.NavigationManager
import com.dxmxp.ui.screens.WebView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BeersViewModel @Inject constructor(
    private val getBeersUseCase: GetBeersUseCase,
    private val beerUiMapper: BeerUiMapper,
    private val navigationManager: NavigationManager
) : BaseViewModel<BeersViewModel.State, BeersViewModel.Effect, BeersViewModel.Event>() {

    data class State(
        val beers: List<BeerUiModel>? = null,
        val isLoading: Boolean? = null,
        val isRefreshing: Boolean? = null,
        val error: String? = null,
        val endReached: Boolean? = null
    ) {
        val canLoadNextPage: Boolean get() = (isLoading != true) && (endReached != true)
        val launchIf: Boolean get() = (isLoading != true) && (isRefreshing != true)
    }

    interface Event {
        data object LoadBeers : Event
        data object LoadNextPage : Event
        data object Refresh : Event
        data class OnBeerClicked(val beer: BeerUiModel) : Event
    }

    interface Effect {
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
            is Event.OnBeerClicked -> {
                navigationManager.push(WebView(url = "https://www.google.com/search?q=${event.beer.name}"))
            }
        }
    }

    init {
        setState { copy(isLoading = false, isRefreshing = false, endReached = false) }
        setEvent(Event.LoadBeers)
    }

    private fun fetchBeers(shouldReset: Boolean, isRefreshing: Boolean = false) {
        val flow = getBeersUseCase(shouldReset)
            .mapData { beerUiMapper.toUiModel(it) }
        
        viewModelScope.collectPagingInto(
            flow = flow,
            setState = ::setState,
            shouldReset = shouldReset,
            currentList = { beers },
            launchIf = uiState.value.launchIf
        ) { combinedList, endReached, exception ->
            if (exception != null) {
                copy(error = exception.message, isLoading = false, isRefreshing = false)
            } else if (combinedList.isEmpty() && !endReached) {
                // Loading state (simplified mapping for this specific VM)
                copy(
                    isLoading = !isRefreshing,
                    isRefreshing = isRefreshing
                )
            } else {
                // Success state
                copy(
                    beers = combinedList,
                    endReached = endReached,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }
}
