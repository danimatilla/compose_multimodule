package com.example.seed.navigation.screens

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.example.seed.navigation.NavigationHandler
import kotlinx.serialization.Serializable


/**
 * Screen representa una pantalla individual en la aplicación. Cada Screen es un NavKey que puede ser
 * agregado al backStack de navegación. Las pantallas pueden ser parte de un Graph, lo que permite
 * organizar la navegación en secciones lógicas. Al navegar a una pantalla, se agrega al backStack,
 * y al hacer pop, se regresa a la pantalla anterior.
 */
@Serializable
sealed interface Screen : NavKey {

    @Serializable
    data object Home : Screen

    /**
     * Obtiene el Graph al que pertenece esta [screen], si es que forma parte de uno.
     */
    val graph: Graph? get() {
        val screenClass = this::class.java
        val enclosingClass = screenClass.enclosingClass ?: return null

        // Obtener la clase Kotlin correspondiente
        val kotlinClass = enclosingClass.kotlin

        // Si es un objeto (object), obtener la instancia
        val instance = kotlinClass.objectInstance

        // Intentar castear a Graph
        return instance as? Graph
    }
}


/**
 * Un Graph es una colección de pantallas relacionadas que comparten un contexto de navegación común.
 * Cada Graph puede tener su propio backStack, lo que permite una navegación independiente dentro de ese Graph.
 * Esto es especialmente útil para secciones de la aplicación que tienen múltiples pantallas, como un
 * perfil de usuario con pantallas de configuración, detalles, etc. Al navegar a un Graph, se puede especificar
 * una pantalla inicial dentro de ese Graph, y al hacer pop desde la raíz del Graph,
 * se regresará al Graph padre o a la pantalla anterior en el stack de navegación principal.
 */
@Serializable
sealed interface Graph : Screen {

    @Composable
    fun NavDisplay(onEvent: (NavigationHandler.NavigationEvent) -> Unit)
}
