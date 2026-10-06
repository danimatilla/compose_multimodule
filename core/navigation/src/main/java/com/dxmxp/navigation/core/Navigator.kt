package com.dxmxp.navigation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.model.Route

/**
 * DSL marker annotation used to scope navigation stack builder functions.
 */
@DslMarker
annotation class NavigationDslMarker

/**
 * Navigator wrapping Navigation 3's [NavBackStack] to manage navigation state and stack operations.
 *
 * Provides atomic stack modifications via [updateStack] and DSL-driven navigation actions
 * via [navAction].
 *
 * @property backStack The underlying Navigation 3 backstack holding [NavKey] elements.
 */
class Navigator(private val backStack: NavBackStack<NavKey>) {

    /**
     * Builder class for constructing and mutating a list of navigation routes atomically.
     *
     * @param initialRoutes Initial collection of routes copied into the builder buffer.
     */
    @NavigationDslMarker
    class StackBuilder internal constructor(initialRoutes: List<NavKey>) {
        private val routes = initialRoutes.toMutableList()

        /**
         * Clears the current stack buffer and sets [route] as the sole root element.
         *
         * @param route The new root route.
         */
        fun root(route: Route) {
            routes.clear()
            routes.add(route)
        }

        /**
         * Pushes [route] onto the top of the stack buffer if it is not already the top element.
         *
         * @param route The route to push onto the stack.
         */
        fun push(route: Route) {
            if (routes.lastOrNull() != route) {
                routes.add(route)
            }
        }

        /**
         * Removes the top element from the stack buffer, keeping at least 1 element if available.
         */
        fun pop() {
            if (routes.size > 1) {
                routes.removeLastOrNull()
            }
        }

        /**
         * Pops elements back to and including the given [route].
         *
         * @param route The target destination route to pop to.
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
     * Executes atomic modifications to the navigation stack inside a [StackBuilder] DSL block.
     * Re-renders the UI only once upon completion of all requested changes.
     *
     * Example:
     * ```
     * navigator.updateStack {
     *     root(MainGraph.Home)
     *     push(StoriesGraph.Home)
     *     push(StoriesGraph.StoryDetail(id = "123"))
     * }
     * ```
     *
     * @param block Lambda scoped to [StackBuilder] configuring the updated stack layout.
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

    /**
     * Dispatches a high-level declarative [NavAction] to mutate the navigation stack.
     *
     * @param action The navigation action to execute.
     */
    fun navAction(action: NavAction) {
        when (action) {
            is NavAction.Push -> push(action.route)
            is NavAction.Pop -> pop()
            is NavAction.PopTo -> popTo(action.route)
            is NavAction.Root -> root(action.route)
            is NavAction.UpdateStack -> updateStack(action.block)
        }
    }

    /**
     * Pushes [route] onto the top of the navigation stack.
     *
     * @param route The target destination route.
     */
    fun push(route: Route) {
        updateStack { push(route) }
    }

    /**
     * Pops the top element off the navigation stack.
     */
    fun pop() {
        updateStack { pop() }
    }

    /**
     * Pops stack destinations back to and including [route].
     *
     * @param route The destination route to return to.
     */
    fun popTo(route: Route) {
        updateStack { popTo(route) }
    }

    /**
     * Clears the entire stack and sets [route] as the new root element.
     *
     * @param route The new root route.
     */
    fun root(route: Route) {
        updateStack { root(route) }
    }

    /**
     * Returns the current top-most destination on the navigation backstack, or `null` if empty.
     */
    val currentDestination: NavKey?
        get() = backStack.lastOrNull()
}

/**
 * Sealed interface defining declarative navigation actions supported by [Navigator].
 */
sealed interface NavAction {

    /**
     * Action to push a new [route] onto the stack.
     *
     * @property route The destination route to push.
     */
    data class Push(val route: Route) : NavAction

    /**
     * Action to pop the top route from the stack.
     */
    data object Pop : NavAction

    /**
     * Action to pop back to a specific [route].
     *
     * @property route The target destination route to pop to.
     */
    data class PopTo(val route: Route) : NavAction

    /**
     * Action to clear the stack and navigate to [route] as the root.
     *
     * @property route The route to set as root.
     */
    data class Root(val route: Route) : NavAction

    /**
     * Action to execute batch modifications on the stack via [Navigator.StackBuilder].
     *
     * @property block The configuration lambda for modifying the stack.
     */
    data class UpdateStack(val block: Navigator.StackBuilder.() -> Unit) : NavAction
}

/**
 * Creates and remembers a [Navigator] instance bound to the provided [backStack].
 *
 * @param backStack The [NavBackStack] instance to be managed.
 * @return A remembered [Navigator] instance.
 */
@Composable
fun rememberNavigator(backStack: NavBackStack<NavKey>): Navigator {
    return remember(backStack) { Navigator(backStack) }
}

/**
 * CompositionLocal providing the current [Navigator] down the Compose hierarchy.
 * Throws an error if accessed before a value is provided via [androidx.compose.runtime.CompositionLocalProvider].
 */
val LocalNavigator: ProvidableCompositionLocal<Navigator> = staticCompositionLocalOf {
    error("No Navigator provided")
}
