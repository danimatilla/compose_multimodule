package com.dxmxp.navigation.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.common.InitializableViewModel

/**
 * Screen represents an individual screen in the application.
 */
interface Screen : Route {

    override val showMainBottomBar: Boolean get() = true

    companion object {

        /**
         * Enhanced entry that automatically handles ViewModel initialization.
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified K : Route, reified VM : ViewModel> EntryProviderScope<NavKey>.screenEntry(
            metadata: Map<String, Any> = emptyMap(),
            crossinline viewModelProvide: @Composable () -> VM,
            crossinline content: @Composable (VM) -> Unit,
        ) {
            entry<K>(metadata = metadata) { route ->
                val viewModel = viewModelProvide()

                LaunchedEffect(route) {
                    (viewModel as? InitializableViewModel<K>)?.init(route)
                }

                if (viewModel !is InitializableViewModel<*>) {
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
