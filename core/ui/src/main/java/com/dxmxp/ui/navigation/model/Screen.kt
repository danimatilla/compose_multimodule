package com.dxmxp.ui.navigation.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.common.InitializableViewModel
import com.dxmxp.ui.navigation.core.LocalNavigator
import com.dxmxp.ui.navigation.core.Navigator
import com.dxmxp.ui.screens.WebView
import com.dxmxp.ui.screens.WebViewScreen


/**
 * Screen represents an individual screen in the application. Each Screen is a NavKey that can be
 * added to the navigation backStack. Screens can be part of a Graph, allowing
 * organization of navigation into logical sections. When navigating to a screen, it's added to the backStack,
 * and when popping, it returns to the previous screen.
 */
interface Screen : Route {

    override val route: String
        get() = Route.calculateRoute(this::class.java)

    override val showMainBottomBar: Boolean get() = true

    companion object {

        /**
         * Enhanced entry that automatically handles ViewModel initialization.
         *
         * If the [Route] type [K] has parameters (data class), it forces the [VM] to implement
         * [InitializableViewModel] and calls its [InitializableViewModel.init] method.
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified K : Route, reified VM : ViewModel> EntryProviderScope<NavKey>.screenEntry(
            metadata: Map<String, Any> = emptyMap(),
            crossinline viewModelProvide: @Composable () -> VM,
            crossinline content: @Composable (VM) -> Unit,
        ) {
            val isSingleton = K::class.java.declaredFields.any { it.name == "INSTANCE" }

            entry<K>(metadata = metadata) { route ->
                val viewModel = viewModelProvide()

                LaunchedEffect(route) {
                    (viewModel as? InitializableViewModel<K>)?.init(route)
                }

                if (viewModel !is InitializableViewModel<*> && (!isSingleton)) {
                    error("Route ${K::class.simpleName} has parameters but ViewModel ${VM::class.simpleName} does not implement InitializableViewModel")
                }

                content(viewModel)
            }
        }

        /**
         * Simple entry for routes that don't need a ViewModel or complex initialization.
         */
        inline fun <reified K : Route> EntryProviderScope<NavKey>.screenEntry(
            metadata: Map<String, Any> = emptyMap(),
            crossinline content: @Composable (K) -> Unit,
        ) {
            entry<K>(metadata = metadata) { route -> content(route) }
        }
    }
}
