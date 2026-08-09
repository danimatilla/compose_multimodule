package com.dxmxp.ui.navigation.core

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A store for passing large data between screens and handling optional results.
 * This avoids passing large objects through navigation routes, preventing
 * TransactionTooLargeException and keeping routes clean for Deep Linking.
 */
@Singleton
class NavigationStore @Inject constructor() {
    private val dataCache = mutableMapOf<String, Any>()
    private val resultFlows = mutableMapOf<String, MutableSharedFlow<Any>>()

    /**
     * Pushes data to be consumed by the next screen.
     */
    fun pushData(key: String, data: Any) {
        dataCache[key] = data
    }

    /**
     * Gets and removes data from the store. Use this to consume data pushed by a previous screen.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getData(key: String, remove: Boolean = true): T? {
        val data = dataCache[key]
        if (remove) dataCache.remove(key)
        return data as? T
    }

    /**
     * Observes for a result associated with a specific key.
     * The collector will receive data only if [emitResult] is called with the same key.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> observeResult(key: String): Flow<T> {
        val flow = resultFlows.getOrPut(key) {
            MutableSharedFlow(
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }
        return flow.asSharedFlow() as Flow<T>
    }

    /**
     * Emits a result for a specific key. Any observer subscribed via [observeResult]
     * with this key will receive the data.
     */
    fun emitResult(key: String, data: Any) {
        resultFlows[key]?.tryEmit(data)
    }

    /**
     * Clears all observers and data for a specific key.
     */
    fun clear(key: String) {
        dataCache.remove(key)
        resultFlows.remove(key)
    }
}
