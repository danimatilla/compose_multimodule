package com.dxmxp.ui.navigation.scaffold

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable

/**
 * Handles back press events with optional screen-level validation.
 * Allows screens to prevent back navigation (e.g., "You have unsaved changes")
 */
interface BackPressHandler {
    /**
     * Called when back is pressed.
     * Return true to allow back press, false to prevent it.
     */
    suspend fun onBackPressed(): Boolean
}

object DefaultBackPressHandler : BackPressHandler {
    override suspend fun onBackPressed(): Boolean = true
}

/**
 * Composition local to provide back press handler at screen level.
 */
val LocalBackPressHandler = compositionLocalOf<BackPressHandler> {
    DefaultBackPressHandler
}

/**
 * Helper function to create a custom BackPressHandler with a lambda.
 */
@Composable
fun rememberBackPressHandler(handler: suspend () -> Boolean): BackPressHandler {
    return remember {
        object : BackPressHandler {
            override suspend fun onBackPressed(): Boolean = handler()
        }
    }
}
