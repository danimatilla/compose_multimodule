package com.dxmxp.data.common


/**
 * A generic class for handling paginated data fetching.
 * It manages the current page key, page size, and whether the last page has been reached.
 *
 * @param K The type of the pagination key (e.g., page number, offset).
 * @param T The type of the items being fetched.
 * @param initialKey The initial key to start pagination from.
 * @param nextKeyProvider A function that takes the current key and page size, and returns the next key for pagination.
 * @param pageSize The number of items to fetch per page (default is 20).
 */
class Paginator<K, T>(
    private val initialKey: K,
    private val nextKeyProvider: (key: K, size: Int) -> K,
    private val pageSize: Int = 20
) {
    private var currentKey: K? = initialKey
    private var isLastPage: Boolean = false

    /**
     * A helper function for fetching paginated data.
     * It takes a suspend function that performs the actual data fetching based on the current key and page size.
     */
    suspend fun fetchPaged(
        fetch: suspend (key: K, limit: Int) -> List<T>?
    ): List<T>? {
        val key = currentKey ?: return null
        if (isLastPage) return null

        val items = fetch(key, pageSize) ?: return null

        // Determine if the last page has been reached based on the number of items fetched.
        isLastPage = items.size < pageSize

        // Update the current key for the next fetch if there are more pages to load.
        currentKey = if (isLastPage) null else nextKeyProvider(key, pageSize)

        return items
    }

    /**
     * A helper function for fetching data that doesn't require pagination.
     * It uses the same fetcher logic but assumes that there are no more pages to load after the initial fetch.
     */
    suspend fun fetchOneShot(
        fetch: suspend () -> List<T>?
    ): List<T>? {
        if (isLastPage) return null
        val items = fetch()

        // Mark as last page since this is a one-shot fetch.
        isLastPage = true

        // Clear the current key since there are no more pages.
        currentKey = null

        return items
    }

    fun reset() {
        currentKey = initialKey
        isLastPage = false
    }

    fun isLastPage(): Boolean = isLastPage
}
