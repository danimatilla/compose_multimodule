package com.dxmxp.navigation.model

import androidx.navigation3.runtime.NavKey

/**
 * Common interface for all navigation keys.
 * Routes should be @Serializable classes or objects.
 */
interface Route : NavKey {
    val route: String
    val showMainBottomBar: Boolean get() = true
    val requiresAuth: Boolean get() = true
}
