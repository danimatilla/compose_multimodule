package com.dxmxp.ui.navigation.model

import androidx.navigation3.runtime.NavKey

interface Route : NavKey {
    val route: String
    val showMainBottomBar: Boolean
    val requiresAuth: Boolean get() = true

    companion object {
        /**
         * Calcula una ruta jerárquica basada en el anidamiento de clases.
         * Si la clase está definida dentro de otro [Route] (como un [Graph]),
         * añade automáticamente el prefijo de la ruta del padre.
         */
        fun calculateRoute(clazz: Class<out Route>): String {
            val name = clazz.simpleName.lowercase()
                .replace("graph", "")
                .replace("screen", "")
            val enclosingClass = clazz.enclosingClass

            if ((enclosingClass != null) && Route::class.java.isAssignableFrom(enclosingClass)) {
                return try {
                    val parent = enclosingClass.getField("INSTANCE")[null] as Route
                    "${parent.route}/$name".replace("//", "/")
                } catch (_: Exception) {
                    "/$name"
                }
            }
            return "/$name"
        }
    }
}
