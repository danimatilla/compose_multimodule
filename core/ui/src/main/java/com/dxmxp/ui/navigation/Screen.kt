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

    val showSeedBottomBar: Boolean get() = true

    /**
     * Busca recursivamente si esta pantalla está anidada dentro de un Grafo
     * y devuelve el valor de showSeedBottomBar de dicho Grafo.
     */
    val inheritedShowSeedBottomBar: Boolean
        get() {
            // Si la propia llave es un Grafo, usamos su valor directamente
            if (this is Graph) return this.showSeedBottomBar

            // Obtenemos la clase donde está definida esta pantalla (la clase envolvente)
            var enclosingClass = this::class.java.enclosingClass

            while (enclosingClass != null) {
                // Si la clase envolvente es un Graph...
                if (Graph::class.java.isAssignableFrom(enclosingClass)) {
                    // Intentamos obtener la instancia del objeto (singleton de Kotlin)
                    val graphInstance = try {
                        enclosingClass.getField("INSTANCE")[null] as? Graph
                    } catch (_: Exception) {
                        null
                    }
                    // Si encontramos el Grafo, él manda sobre la visibilidad
                    if (graphInstance != null) return graphInstance.showSeedBottomBar
                }
                // Seguimos subiendo por si hay grafos anidados (Graph dentro de Graph)
                enclosingClass = enclosingClass.enclosingClass
            }

            // Si no hay padre o no es un Grafo, usamos el valor individual de la pantalla
            return (this as? Screen)?.showSeedBottomBar ?: true
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
