package com.dxmxp.navigation.model

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * A Graph is a collection of related screens that share a common navigation context.
 */
interface Graph : Route {
    override val showMainBottomBar: Boolean get() = true
    
    val isModal: Boolean get() = false

    fun EntryProviderScope<NavKey>.registerScreens()
}
