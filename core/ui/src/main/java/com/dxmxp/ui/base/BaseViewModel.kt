package com.dxmxp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    protected abstract fun createInitialState(): STATE

    protected abstract fun handleEvent(event: EVENT)
}
