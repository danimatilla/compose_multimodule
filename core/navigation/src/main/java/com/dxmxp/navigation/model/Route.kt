package com.dxmxp.navigation.model

import androidx.navigation3.runtime.NavKey

/**
 * Common interface for all navigation keys.
 * Routes should be @Serializable classes or objects.
 */
interface Route : NavKey {
    val route: String
    val showMainBottomBar: Boolean get() = !isModal(this)
    val requiresAuth: Boolean get() = true

    companion object {
        /**
         * Verifies if a Route instance is annotated with @ModalRoute.
         */
        fun isModal(route: Route?): Boolean {
            return route?.javaClass?.isAnnotationPresent(ModalRoute::class.java) == true
        }

        /**
         * Verifies if a Route Class is annotated with @ModalRoute.
         */
        fun isModal(clazz: Class<out Route>): Boolean {
            return clazz.isAnnotationPresent(ModalRoute::class.java)
        }

        /**
         * Dynamically resolves whether type K : Route is annotated with @ModalRoute.
         */
        inline fun <reified K : Route> resolveIsModal(): Boolean {
            return isModal(K::class.java)
        }
    }

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class ModalRoute
}
