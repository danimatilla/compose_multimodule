package com.dxmxp.navigation.model

import androidx.navigation3.runtime.NavKey

/**
 * Common interface for all navigation keys.
 * Routes should be @Serializable classes or objects.
 */
interface Route : NavKey {
    val route: String
    val isModal: Boolean get() = false
    val showMainBottomBar: Boolean get() = !isModal
    val requiresAuth: Boolean get() = true
}
