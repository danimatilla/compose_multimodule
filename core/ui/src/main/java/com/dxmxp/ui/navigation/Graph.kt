package com.dxmxp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
interface Graph : Screen, NavigationContributor {

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

    fun EntryProviderScope<NavKey>.registerCommonEntries() {
        screenEntry<WebView> { webView ->
            WebViewScreen(screen = webView)
        }
    }

    fun EntryProviderScope<NavKey>.registerEntries()

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

        @Composable
        fun HandleGraphEvents(
            backStack: androidx.navigation3.runtime.NavBackStack<NavKey>,
            graph: Graph,
        ) {
            val navigator = LocalNavigator.current
            val dataObserver = LocalDataObserver.current
            LaunchedEffect(backStack) {
                navigator.events.collect { event ->
                    val screen = when (event) {
                        is NavigationHandler.NavigationEvent.PushScreen -> event.screen
                        is NavigationHandler.NavigationEvent.SetRootScreen -> event.screen
                        is NavigationHandler.NavigationEvent.PopScreen -> event.screen
                    }

                    if (screen != null && graph.contains(screen)) {
                        NavigationHandler.handleEvent(backStack, event, dataObserver)
                    } else if (event is NavigationHandler.NavigationEvent.PopScreen && event.screen == null && backStack.size > 1) {
                        NavigationHandler.handleEvent(backStack, event, dataObserver)
                    }
                }
            }
        }
    }
}

interface NavigationContributor {
    fun provideGraph(): Graph
}
