package com.dxmxp.domain.common

import com.dxmxp.domain.AppException

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class DataResult<out T> {
    data class Success<out T>(
        val data: T,
        val endReached: Boolean = true
    ) : DataResult<T>() {
        override fun toString() = "DataResult::Success\n$data (endReached=$endReached)"
    }
    data class Error(val exception: AppException) : DataResult<Nothing>() {
        override fun toString() = "DataResult::Error\n${exception.message}"
    }
    data object Loading : DataResult<Nothing>() {
        override fun toString() = "DataResult::Loading"
    }

    /**
     * Returns the data if the result is [Success], or null otherwise.
     */
    fun getOrNull(): T? = (this as? Success)?.data

    companion object
}

/**
 * Exhaustive fold for [DataResult] to ensure all states are handled.
 */
inline fun <T, R> DataResult<T>.fold(
    onLoading: (Boolean) -> R,
    onSuccess: (data: T, endReached: Boolean) -> R,
    onError: (AppException) -> R
): R = when (this) {
    is DataResult.Loading -> onLoading(true)
    is DataResult.Success -> onSuccess(data, endReached)
    is DataResult.Error -> onError(exception)
}

inline fun <T> DataResult<T>.onSuccess(action: (data: T, endReached: Boolean) -> Unit): DataResult<T> {
    if (this is DataResult.Success) action(data, endReached)
    return this
}

inline fun <T> DataResult<T>.onError(action: (AppException) -> Unit): DataResult<T> {
    if (this is DataResult.Error) action(exception)
    return this
}

inline fun <T> DataResult<T>.onLoading(action: (Boolean) -> Unit): DataResult<T> {
    if (this is DataResult.Loading) action(true)
    else action(false)
    return this
}
