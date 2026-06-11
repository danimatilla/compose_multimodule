package com.dxmxp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<STATE, EFFECT, EVENT> : ViewModel() {

    private val initialState: STATE by lazy { createInitialState() }

    private val _uiState: MutableStateFlow<STATE> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val events: MutableSharedFlow<EVENT> = MutableSharedFlow()

    private val _effect = Channel<EFFECT>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        subscribeIntents()
    }

    private fun subscribeIntents() {
        viewModelScope.launch {
            events.collect {
                handleEvent(it)
            }
        }
    }

    fun setEvent(event: EVENT) {
        viewModelScope.launch { events.emit(event) }
    }

    protected fun setState(reduce: STATE.() -> STATE) {
        _uiState.value = uiState.value.reduce()
    }

    protected fun setEffect(builder: () -> EFFECT) {
        viewModelScope.launch { _effect.send(builder()) }
    }

    /**
     * Launches a Flow and automatically manages loading, error, and success states.
     * [onLoading] and [onError] are optional in case some states do not require them.
     */
    protected fun <T> launchFlow(
        flow: Flow<T>,
        onLoading: (STATE.(Boolean) -> STATE)? = null,
        onError: (STATE.(String?) -> STATE)? = null,
        onSuccess: STATE.(T) -> STATE
    ) {
        viewModelScope.launch {
            flow
                .onStart {
                    setState {
                        var state = this
                        onLoading?.let { state = state.it(true) }
                        onError?.let { state = state.it(null) }
                        state
                    }
                }
                .catch { e ->
                    val msg = e.localizedMessage ?: "Unknown error"
                    setState {
                        var state = this
                        onLoading?.let { state = state.it(false) }
                        onError?.let { state = state.it(msg) }
                        state
                    }
                }
                .collect { data ->
                    setState {
                        var state = this
                        onLoading?.let { state = state.it(false) }
                        state.onSuccess(data)
                    }
                }
        }
    }

    protected abstract fun createInitialState(): STATE

    protected abstract fun handleEvent(event: EVENT)
}
