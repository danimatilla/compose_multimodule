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

    val showMainBottomBar: Boolean get() = true

    /**
     * Determina si esta pantalla pertenece a un grafo modal,
     * ya sea porque ella misma es un grafo modal o porque está dentro de uno.
     */
    fun belongModalGraph(): Boolean? {
        // 1. Si la pantalla misma es un Grafo y es modal
        if (this is Graph && this.isModal) return true

        // 2. Buscamos en las clases que envuelven a esta pantalla (enclosing classes)
        var enclosingClass = this::class.java.enclosingClass
        while (enclosingClass != null) {
            // Si la clase que la envuelve implementa Graph...
            if (Graph::class.java.isAssignableFrom(enclosingClass)) {
                // Intentamos obtener la instancia singleton (object en Kotlin)
                val graphInstance = try {
                    enclosingClass.getField("INSTANCE")[null] as? Graph
                } catch (_: Exception) {
                    null
                }
                // Si el grafo es modal, devolvemos true
                if (graphInstance?.isModal == true) return true
            }
            // Seguimos subiendo por si hay grafos anidados
            enclosingClass = enclosingClass.enclosingClass
        }

        return false
    }

    companion object{

        /**
         * Enhanced entry that automatically handles ViewModel initialization.
         *
         * If the [Screen] type [K] has parameters (data class), it forces the [VM] to implement
         * [InitializableViewModel] and calls its [InitializableViewModel.init] method.
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified K : Screen, reified VM : ViewModel> EntryProviderScope<NavKey>.screenEntry(
            metadata: Map<String, Any> = emptyMap(),
            crossinline viewModelProvide: @Composable () -> VM,
            crossinline content: @Composable (VM) -> Unit,
        ) {
            val isSingleton = K::class.java.declaredFields.any { it.name == "INSTANCE" }

            entry<K>(metadata = metadata) { screen ->
                val viewModel = viewModelProvide()

                LaunchedEffect(screen) {
                    (viewModel as? InitializableViewModel<K>)?.init(screen)
                }

                if (viewModel !is InitializableViewModel<*> && (!isSingleton)) {
                    error("Screen ${K::class.simpleName} has parameters but ViewModel ${VM::class.simpleName} does not implement InitializableViewModel")
                }

                content(viewModel)
            }
        }

        /**
         * Simple entry for screens that don't need a ViewModel or complex initialization.
         */
        inline fun <reified K : Screen> EntryProviderScope<NavKey>.screenEntry(
            metadata: Map<String, Any> = emptyMap(),
            crossinline content: @Composable (K) -> Unit,
        ) {
            entry<K>(metadata = metadata) { screen -> content(screen) }
        }
    }
}
