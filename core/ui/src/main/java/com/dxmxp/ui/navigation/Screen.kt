package com.dxmxp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.base.InitializableViewModel


/**
 * Screen represents an individual screen in the application. Each Screen is a NavKey that can be
 * added to the navigation backStack. Screens can be part of a Graph, allowing
 * organization of navigation into logical sections. When navigating to a screen, it's added to the backStack,
 * and when popping, it returns to the previous screen.
 */
interface Screen : NavKey {

    val route: String

    val showBottomBar: Boolean get() = true
}


/**
 * A Graph is a collection of related screens that share a common navigation context.
 * In a common backstack architecture, a Graph provides the entries (EntryProvider) for its
 * screens, allowing the root to integrate them into a single navigation stack.
 */
interface Graph : Screen {

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
            clazz.isInstance(key) || (
                try {
                    val instance = clazz.getField("INSTANCE")[null] as? Graph
                    instance?.contains(key) == true
                } catch (_: Exception) {
                    false
                }
            )
        }

    fun EntryProviderScope<NavKey>.registerEntries(onEvent: (NavigationHandler.NavigationEvent) -> Unit)
}

/**
 * Enhanced entry that automatically handles ViewModel initialization.
 *
 * If the [Screen] type [T] has parameters (data class), it forces the [VM] to implement
 * [InitializableViewModel] and calls its [InitializableViewModel.init] method.
 */
inline fun <reified T : Screen, reified VM : ViewModel> EntryProviderScope<NavKey>.screenEntry(
    crossinline viewModelProvide: @Composable () -> VM,
    crossinline content: @Composable (VM) -> Unit,
) {
    val isSingleton = T::class.java.declaredFields.any { it.name == "INSTANCE" }

    entry<T> { screen ->
        val viewModel = viewModelProvide()

        LaunchedEffect(screen) {
            @Suppress("UNCHECKED_CAST")
            (viewModel as? InitializableViewModel<T>)?.init(screen)
        }

        if (viewModel !is InitializableViewModel<*> && (!isSingleton)) {
            error("Screen ${T::class.simpleName} has parameters but ViewModel ${VM::class.simpleName} does not implement InitializableViewModel")
        }

        content(viewModel)
    }
}

/**
 * Simple entry for screens that don't need a ViewModel or complex initialization.
 */
inline fun <reified T : Screen> EntryProviderScope<NavKey>.screenEntry(
    crossinline content: @Composable (T) -> Unit,
) {
    entry<T> { content(it) }
}
