package com.dxmxp.domain.common

import com.dxmxp.domain.AppException

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val exception: AppException) : DataResult<Nothing>()
    data object Loading : DataResult<Nothing>()

    /**
     * Returns the data if the result is [Success], or null otherwise.
     */
    fun getOrNull(): T? = (this as? Success)?.data

    override fun toString(): String {
        return when (this) {
            is Loading -> "DataResult.Loading"
            is Success -> {
                val data = this.data
                if (data is List<*>) {
                    "DataResult.Success(itemsCount=${data.size})"
                } else {
                    "DataResult.Success(data=$data)"
                }
            }
            is Error -> "DataResult.Error(exception=${exception.message})"
        }
    }

    companion object
}