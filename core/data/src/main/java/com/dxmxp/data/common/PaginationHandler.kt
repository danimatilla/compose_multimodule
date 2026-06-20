package com.dxmxp.data.common

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * A helper class to manage the state of paginated data in a repository.
 */
class PaginationHandler<T> @AssistedInject constructor(
    @Assisted private val pageSize: Int
) {
    private val _cachedItems = mutableListOf<T>()
    var isEndReached = false
        private set

    @AssistedFactory
    interface Factory<T> {
        fun create(pageSize: Int): PaginationHandler<T>
    }

    fun processResult(newItems: List<T>?, shouldReset: Boolean): List<T>? {
        if (shouldReset) {
            _cachedItems.clear()
            isEndReached = false
        }

        if (newItems != null) {
            _cachedItems.addAll(newItems)
            isEndReached = newItems.size < pageSize
        } else {
            isEndReached = true
        }

        return if (_cachedItems.isEmpty() && newItems == null) null else _cachedItems.toList()
    }
}
