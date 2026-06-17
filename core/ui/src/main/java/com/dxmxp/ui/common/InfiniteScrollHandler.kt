package com.dxmxp.ui.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

/**
 * A reusable handler to detect when a [LazyListState] reaches the end of its items.
 */
@Composable
fun InfiniteScrollHandler(
    listState: LazyListState,
    isLoading: Boolean,
    endReached: Boolean,
    buffer: Int = 1,
    onLoadNextPage: () -> Unit
) {
    InfiniteScrollHandlerInternal(
        shouldLoadMoreProvider = {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItem != null && lastVisibleItem.index >= totalItemsCount - 1 - buffer
        },
        isLoading = isLoading,
        endReached = endReached,
        onLoadNextPage = onLoadNextPage
    )
}

/**
 * A reusable handler to detect when a [LazyGridState] reaches the end of its items.
 */
@Composable
fun InfiniteScrollHandler(
    gridState: LazyGridState,
    isLoading: Boolean,
    endReached: Boolean,
    buffer: Int = 1,
    onLoadNextPage: () -> Unit
) {
    InfiniteScrollHandlerInternal(
        shouldLoadMoreProvider = {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItemsCount = gridState.layoutInfo.totalItemsCount
            lastVisibleItem != null && lastVisibleItem.index >= totalItemsCount - 1 - buffer
        },
        isLoading = isLoading,
        endReached = endReached,
        onLoadNextPage = onLoadNextPage
    )
}

/**
 * Internal common logic for infinite scroll detection.
 */
@Composable
private fun InfiniteScrollHandlerInternal(
    shouldLoadMoreProvider: () -> Boolean,
    isLoading: Boolean,
    endReached: Boolean,
    onLoadNextPage: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf { shouldLoadMoreProvider() }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !isLoading && !endReached) {
            onLoadNextPage()
        }
    }
}
