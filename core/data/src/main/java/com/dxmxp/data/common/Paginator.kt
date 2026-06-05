package com.dxmxp.data.common


/**
 * A generic class for handling paginated data fetching.
 * It manages the current page key, page size, and whether the last page has been reached.
 *
 * @param K The type of the pagination key (e.g., page number, offset).
 * @param R The type of the items being paginated.
 * @param initialKey The initial key to start pagination from.
 * @param nextKeyProvider A function that takes the current key and page size, and returns the next key for pagination.
 */
class Paginator<K, R>(
    private val initialKey: K,
    private val pageSize: Int = 20,
    private val nextKeyProvider: (key: K, result: R, pageSize: Int) -> K?,
) {
    private var currentKey: K? = initialKey

    /**
     * A helper function for fetching paginated data.
     * It takes a suspend function that performs the actual data fetching based on the current key and page size.
     */
    suspend fun fetchPaged(
        fetch: suspend (key: K, limit: Int) -> R?
    ): R? {
        val key = currentKey ?: return null
        val result = fetch(key, pageSize) ?: return null

        currentKey = nextKeyProvider(key, result, pageSize)

        return result
    }

    /**
     * A helper function for fetching data that doesn't require pagination.
     * It uses the same fetcher logic but assumes that there are no more pages to load after the initial fetch.
     */
    suspend fun fetchOneShot(
        fetch: suspend () -> R?
    ): R? {
        val items = fetch()
        currentKey = null
        return items
    }

    fun reset() {
        currentKey = initialKey
    }
}
