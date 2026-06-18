package com.dxmxp.domain.common

import com.dxmxp.domain.AppException

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>() {
        override fun toString() = "DataResult::Success\n$data"
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