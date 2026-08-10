package com.dxmxp.ui.navigation.core

import android.net.Uri
import android.util.Log
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.model.Graph
import com.dxmxp.ui.navigation.model.Route
import com.dxmxp.ui.navigation.model.Screen
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.full.primaryConstructor

/**
 * Centralized registry of all routes and graphs in the application.
 * Provides safe route resolution without reflection and metadata lookup.
 */
@Singleton
class RouteRegistry @Inject constructor(
    graphs: Set<@JvmSuppressWildcards Graph>
) {
    private val routeRegistry = mutableMapOf<String, RouteEntry>()
    private val graphRegistry = mutableMapOf<String, Graph>()
    
    init {
        graphs.forEach { graph ->
            registerGraph(graph)
        }
        Log.d(TAG, "✅ RouteRegistry initialized with ${routeRegistry.size} routes")
    }

    /**
     * Registers a graph and all its child screens.
     */
    private fun registerGraph(graph: Graph) {
        graphRegistry[graph.route] = graph
        
        graph.screens.forEach { routeClass ->
            if (routeClass == graph::class.java) {
                // Register the graph itself as a singleton
                routeRegistry[graph.route] = RouteEntry.Singleton(graph)
                Log.d(TAG, "  📍 Registered graph: ${graph.route}")
                return@forEach
            }

            try {
                // Try to get singleton instance (data object)
                val instance = try {
                    routeClass.getField("INSTANCE").get(null) as? Route
                } catch (_: Exception) {
                    null
                }

                if (instance != null) {
                    routeRegistry[instance.route] = RouteEntry.Singleton(instance)
                    Log.d(TAG, "  📍 Registered singleton: ${instance.route}")
                    // Also register subgraphs
                    if (instance is Graph) {
                        registerGraph(instance)
                    }
                } else {
                    // It's a parameterized route (data class)
                    // We check if it's a Screen class
                    @Suppress("UNCHECKED_CAST")
                    if (Screen::class.java.isAssignableFrom(routeClass)) {
                        registerParameterizedScreen(routeClass as Class<out Screen>)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to register route ${routeClass.simpleName}: ${e.message}")
            }
        }
    }

    /**
     * Registers a screen that accepts parameters.
     */
    private fun registerParameterizedScreen(screenClass: Class<out Screen>) {
        val baseRoute = "/" + screenClass.simpleName.lowercase()
        routeRegistry[baseRoute] = RouteEntry.Parameterized(screenClass)
        Log.d(TAG, "  📦 Registered parameterized: $baseRoute (${screenClass.simpleName})")
    }

    /**
     * Finds a route by exact route match.
     */
    fun getRouteByRoute(route: String): Route? {
        return (routeRegistry[route] as? RouteEntry.Singleton)?.route
    }

    /**
     * Creates a route instance from a deep link URI.
     */
    fun createRouteFromUri(uri: Uri): Route? {
        val path = uri.path ?: return null
        
        // Try exact match first
        getRouteByRoute(path)?.let { return it }
        
        // Try parameterized route
        val entry = routeRegistry.values
            .filterIsInstance<RouteEntry.Parameterized>()
            .firstOrNull { path.startsWith(it.baseRoute) }
            ?: return null

        return createScreenWithParams(entry.screenClass, uri)
    }

    /**
     * Creates a screen instance with parameters from URI query params.
     */
    private fun createScreenWithParams(
        screenClass: Class<out Screen>,
        uri: Uri
    ): Route? {
        return try {
            val queryParams = uri.queryParameterNames.associateWith { uri.getQueryParameter(it) }
            
            if (queryParams.isEmpty()) {
                return screenClass.getDeclaredConstructor().newInstance()
            }

            val constructor = screenClass.kotlin.primaryConstructor ?: return null
            val args = constructor.parameters.associateWith { param ->
                val paramValue = queryParams[param.name] ?: return null
                when (param.type.classifier) {
                    String::class -> paramValue
                    Int::class -> paramValue.toIntOrNull() ?: return null
                    Boolean::class -> paramValue.toBoolean()
                    Long::class -> paramValue.toLongOrNull() ?: return null
                    else -> return null
                }
            }
            
            constructor.callBy(args) as? Route
        } catch (e: Exception) {
            Log.w(TAG, "Failed to create screen from params: ${e.message}")
            null
        }
    }

    /**
     * Gets all registered graphs.
     */
    fun getAllGraphs(): Set<Graph> = graphRegistry.values.toSet()

    /**
     * Gets all routes for a specific graph.
     */
    fun getRoutesForGraph(graph: Graph): List<Route> {
        return routeRegistry.values
            .filterIsInstance<RouteEntry.Singleton>()
            .map { it.route }
            .filter { graph.contains(it) }
    }

    sealed interface RouteEntry {
        data class Singleton(val route: Route) : RouteEntry
        data class Parameterized(val screenClass: Class<out Screen>) : RouteEntry {
            val baseRoute: String = "/" + screenClass.simpleName.lowercase()
        }
    }

    private companion object {
        const val TAG = "RouteRegistry"
    }
}

