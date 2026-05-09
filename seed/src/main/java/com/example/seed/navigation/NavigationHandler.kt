package com.example.seed.navigation

import android.util.Log
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.seed.navigation.screens.Graph
import com.example.seed.navigation.screens.ProfileGraph
import com.example.seed.navigation.screens.Screen

object NavigationHandler {

    private val initialScreens = mutableMapOf<Graph, Screen>()

    fun eventHandler(
        backStack: NavBackStack<NavKey>,
        event: NavigationEvent
    ) {
        var eventString: String
        val initialBackStack = backStack.stackString()

        when (event) {
            is NavigationEvent.PushScreen -> {
                val screen = event.screen
                backStack.add(event.screen)
                screen.graph?.let { graph ->
                    initialScreens[graph] = screen
                }

                eventString = event.toString()
            }

            is NavigationEvent.PopScreen -> {
                event.screen?.let {
                    // Hacer pop hasta la pantalla específica
                    backStack.removeAll { screen -> screen != event.screen }
                } ?: run {
                    // Hacer pop normal
                    backStack.removeLastOrNull()
                }

                eventString = event.toString()
            }
        }

        val currentBackStack = backStack.stackString()
        Log.d(TAG, "$eventString :: $initialBackStack > $currentBackStack")
    }

    fun getInitialScreen(graph: Graph): Screen {
        return initialScreens.remove(graph) ?: when (graph) {
            // Aquí puedes definir pantallas iniciales por defecto para cada graph si lo deseas.
            ProfileGraph -> ProfileGraph
        }
    }

    sealed interface NavigationEvent {
        /**
         * Agrega una nueva [screen] al stack de navegación.
         */
        data class PushScreen(val screen: Screen) : NavigationEvent {

            override fun toString(): String =
                "${javaClass.simpleName}: ${screen.javaClass.simpleName}"
        }

        /**
         * Regresa a la pantalla anterior. Si se está en la raíz del graph, se regresará al graph padre.
         * Si se proporciona un [screen], se hará pop hasta esa pantalla específica.
         */
        data class PopScreen(val screen: Screen? = null) : NavigationEvent {

            override fun toString(): String =
                "${javaClass.simpleName}: ${screen?.javaClass?.simpleName}"
        }
    }

    private fun NavBackStack<NavKey>.stackString(): String =
        "[ ${joinToString { it.javaClass.simpleName }} ]"

    const val TAG = "NavigationHandler"
}
