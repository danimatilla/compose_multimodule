package com.dxmxp.ui.common

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A reusable Pull-To-Refresh component that wraps the Material 3 implementation.
 *
 * @param isRefreshing Whether the refresh indicator should be shown.
 * @param onRefresh Callback to be invoked when a refresh is triggered.
 * @param modifier Modifier for the container.
 * @param state The state of the pull-to-refresh component.
 * @param indicator The indicator to be shown during refresh.
 * @param content The content that will be refreshable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeedPullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    indicator: @Composable BoxScope.() -> Unit = {
        PullToRefreshDefaults.Indicator(
            state = state,
            isRefreshing = isRefreshing,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    },
    content: @Composable BoxScope.() -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = indicator,
        content = content
    )
}
