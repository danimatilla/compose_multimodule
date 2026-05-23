package com.dxmxp.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.Screen.Companion.screenEntry
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.ui.screens.WebView
import com.dxmxp.ui.screens.WebViewScreen

/**
 * A Graph is a collection of related screens that share a common navigation context.
 * In a common backstack architecture, a Graph provides the entries (EntryProvider) for its
 * screens, allowing the root to integrate them into a single navigation stack.
 */
interface Graph : Screen {

    val isModal: Boolean
        get() = false

    val children: List<Class<out Screen>>
        get() = javaClass.declaredClasses
            .asSequence()
            .filter { Screen::class.java.isAssignableFrom(it) }
            .map {
                @Suppress("UNCHECKED_CAST")
                it as Class<out Screen>
            }
            .toList()

    fun contains(key: NavKey): Boolean =
        children.any { clazz ->
            clazz.isInstance(key) || try {
                val instance = clazz.getField("INSTANCE")[null] as? Graph
                instance?.contains(key) == true
            } catch (_: Exception) {
                false
            }
        }

    fun EntryProviderScope<NavKey>.registerCommonEntries(onEvent: (NavigationHandler.NavigationEvent) -> Unit){
        screenEntry<WebView>{ WebViewScreen(it, onEvent) }
    }

    fun EntryProviderScope<NavKey>.registerEntries(onEvent: (NavigationHandler.NavigationEvent) -> Unit)
}
