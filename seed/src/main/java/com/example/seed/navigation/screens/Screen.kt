package com.example.seed.navigation.screens

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.seed.navigation.NavigationHandler
import kotlinx.serialization.Serializable


/**
 * Screen represents an individual screen in the application. Each Screen is a NavKey that can be
 * added to the navigation backStack. Screens can be part of a Graph, allowing
 * organization of navigation into logical sections. When navigating to a screen, it's added to the backStack,
 * and when popping, it returns to the previous screen.
 */
@Serializable
sealed interface Screen : NavKey {

    val route: String
}


/**
 * A Graph is a collection of related screens that share a common navigation context.
 * In a common backstack architecture, a Graph provides the entries (EntryProvider) for its
 * screens, allowing the root to integrate them into a single navigation stack.
 */
@Serializable
sealed interface Graph : Screen {

    fun EntryProviderScope<NavKey>.registerEntries(onEvent: (NavigationHandler.NavigationEvent) -> Unit)
}
