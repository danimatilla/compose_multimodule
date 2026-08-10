package com.dxmxp.ui.navigation.model

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.model.Screen.Companion.screenEntry
import com.dxmxp.ui.screens.WebView
import com.dxmxp.ui.screens.WebViewScreen

/**
 * A Graph is a collection of related screens that share a common navigation context.
 * In a common backstack architecture, a Graph provides the entries (EntryProvider) for its
 * screens, allowing the root to integrate them into a single navigation stack.
 */
interface Graph : Route {

    override val route: String
        get() = "/" + (this::class.simpleName ?: "").lowercase()

    override val showMainBottomBar: Boolean get() = true

    val isModal: Boolean
        get() = false

    val screens: List<Class<out Route>>

    fun contains(key: NavKey): Boolean =
        javaClass.isInstance(key) || screens.any { clazz ->
            clazz.isInstance(key) || try {
                val instance = clazz.getField("INSTANCE")[null] as? Graph
                if (instance != null && instance !== this) {
                    instance.contains(key)
                } else {
                    false
                }
            } catch (_: Exception) {
                false
            }
        }

    fun EntryProviderScope<NavKey>.registerCommonEntries() {
        screenEntry<WebView> { webView ->
            WebViewScreen(screen = webView)
        }
    }

    fun EntryProviderScope<NavKey>.registerEntries()

    fun provideGraph(): Graph

    companion object {
        fun EntryProviderScope<NavKey>.registerGraphs(
            graphs: Set<Graph>
        ) {
            graphs.firstOrNull()?.apply { registerCommonEntries() }

            graphs.forEach { graph ->
                with(graph) {
                    registerEntries()
                }
            }
        }
    }
}
