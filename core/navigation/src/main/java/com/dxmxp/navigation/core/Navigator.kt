package com.dxmxp.navigation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.model.Route

@DslMarker
annotation class NavigationDslMarker

/**
 * A simple navigator that wraps NavBackStack to provide navigation operations.
 * Supports atomic stack modifications via the [navAction] DSL.
 */
class Navigator(private val backStack: NavBackStack<NavKey>) {

    @NavigationDslMarker
    class StackBuilder internal constructor(initialRoutes: List<NavKey>) {
        private val routes = initialRoutes.toMutableList()

        /**
         * Clears the current stack and sets [route] as the sole root element.
         */
        fun root(route: Route) {
            routes.clear()
            routes.add(route)
        }

        /**
         * Pushes [route] onto the top of the stack if it is not already the top element.
         */
        fun push(route: Route) {
            if (routes.lastOrNull() != route) {
                routes.add(route)
            }
        }

        /**
         * Removes the top element from the stack, keeping at least 1 element if available.
         */
        fun pop() {
            if (routes.size > 1) {
                routes.removeLastOrNull()
            }
        }

        /**
         * Pops elements back to and including the given [route].
         */
        fun popTo(route: Route) {
            val index = routes.indexOfLast { it == route }
            if (index != -1) {
                routes.subList(index + 1, routes.size).clear()
            }
        }

        internal fun build(): List<NavKey> = routes.toList()
    }

    /**
     * Executes atomic modifications to the navigation stack inside a DSL block.
     * Re-renders the UI only once upon completion of all changes.
     *
     * Example:
     * ```
     * navigator.updateStack {
     *     root(MainGraph.Home)
     *     push(StoriesGraph.StoryDetail(id = "123"))
     * }
     * ```
     */
    fun updateStack(block: StackBuilder.() -> Unit) {
        val newRoutes = StackBuilder(backStack).apply(block).build()
        if ((newRoutes.isNotEmpty()) && (newRoutes != backStack)) {
            backStack.run {
                clear()
                newRoutes.forEach { add(it) }
            }
        }
    }

    fun navAction(action: NavAction) {
        when (action) {
            is NavAction.Push -> push(action.route)
            is NavAction.Pop -> pop()
            is NavAction.PopTo -> popTo(action.route)
            is NavAction.Root -> root(action.route)
            is NavAction.UpdateStack -> updateStack(action.block)
        }
    }

    fun push(route: Route) {
        updateStack { push(route) }
    }

    fun pop() {
        updateStack { pop() }
    }

    fun popTo(route: Route) {
        updateStack { popTo(route) }
    }

    fun root(route: Route) {
        updateStack { root(route) }
    }

    val currentDestination: NavKey?
        get() = backStack.lastOrNull()
}

sealed interface NavAction {
    data class Push(val route: Route) : NavAction
    data object Pop : NavAction
    data class PopTo(val route: Route) : NavAction
    data class Root(val route: Route) : NavAction
    data class UpdateStack(val block: Navigator.StackBuilder.() -> Unit) : NavAction
}

@Composable
fun rememberNavigator(backStack: NavBackStack<NavKey>): Navigator {
    return remember(backStack) { Navigator(backStack) }
}

val LocalNavigator: ProvidableCompositionLocal<Navigator> = staticCompositionLocalOf {
    error("No Navigator provided")
}
