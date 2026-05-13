package com.dxmxp.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey


/**
 * Screen represents an individual screen in the application. Each Screen is a NavKey that can be
 * added to the navigation backStack. Screens can be part of a Graph, allowing
 * organization of navigation into logical sections. When navigating to a screen, it's added to the backStack,
 * and when popping, it returns to the previous screen.
 */
interface Screen : NavKey {

    val route: String
}


/**
 * A Graph is a collection of related screens that share a common navigation context.
 * In a common backstack architecture, a Graph provides the entries (EntryProvider) for its
 * screens, allowing the root to integrate them into a single navigation stack.
 */
interface Graph : Screen {

    val children: List<Screen>
        get() = javaClass.declaredClasses
            .filter { Screen::class.java.isAssignableFrom(it) }
            .mapNotNull { clazz ->
                try {
                    clazz.getField("INSTANCE").get(null) as? Screen
                } catch (_: Exception) {
                    null
                }
            }

    fun contains(key: NavKey): Boolean =
        children.any { it == key || (it is Graph && it.contains(key)) }

    fun EntryProviderScope<NavKey>.registerEntries(onEvent: (NavigationHandler.NavigationEvent) -> Unit)
}
