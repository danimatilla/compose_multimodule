package com.dxmxp.ui.base

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DataObserver @Inject constructor() {
    private val _events = MutableSharedFlow<Any>(replay = 1)
    val events = _events.asSharedFlow()

    suspend fun emit(data: Any) {
        _events.emit(data)
    }

    inline fun <reified T> getLast(): T? =
        events.replayCache.lastOrNull() as? T
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DataObserverEntryPoint {
    fun dataObserver(): DataObserver
}
