package com.dxmxp.ui.navigation

import android.net.Uri
import android.util.Log
import androidx.navigation3.runtime.NavKey
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
    private val screenRegistry = mutableMapOf<String, ScreenRouteEntry>()
    private val graphRegistry = mutableMapOf<String, Graph>()
    
    init {
        graphs.forEach { graph ->
            registerGraph(graph)
        }
        Log.d(TAG, "✅ RouteRegistry initialized with ${screenRegistry.size} routes")
    }

    /**
     * Registers a graph and all its child screens.
     */
    private fun registerGraph(graph: Graph) {
        graphRegistry[graph.route] = graph
        
        graph.children.forEach { screenClass ->
            if (screenClass == graph::class.java) {
                // Register the graph itself as a singleton if it's also a screen
                screenRegistry[graph.route] = ScreenRouteEntry.Singleton(graph)
                Log.d(TAG, "  📍 Registered graph screen: ${graph.route}")
                return@forEach
            }

            try {
                // Try to get singleton instance (data object)
                val instance = try {
                    screenClass.getField("INSTANCE").get(null) as? Screen
                } catch (_: Exception) {
                    null
                }

                if (instance != null) {
                    screenRegistry[instance.route] = ScreenRouteEntry.Singleton(instance)
                    Log.d(TAG, "  📍 Registered singleton: ${instance.route}")
                    // Also register subgraphs
                    if (instance is Graph) {
                        registerGraph(instance)
                    }
                } else {
                    // It's a parameterized screen (data class)
                    registerParameterizedScreen(screenClass)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to register screen ${screenClass.simpleName}: ${e.message}")
            }
        }
    }

    /**
     * Registers a screen that accepts parameters.
     */
    private fun registerParameterizedScreen(screenClass: Class<out Screen>) {
        val baseRoute = "/" + screenClass.simpleName.lowercase()
        screenRegistry[baseRoute] = ScreenRouteEntry.Parameterized(screenClass)
        Log.d(TAG, "  📦 Registered parameterized: $baseRoute (${screenClass.simpleName})")
    }

    /**
     * Finds a screen by exact route match.
     */
    fun getScreenByRoute(route: String): Screen? {
        return (screenRegistry[route] as? ScreenRouteEntry.Singleton)?.screen
    }

    /**
     * Creates a screen instance from a deep link URI.
     */
    fun createScreenFromUri(uri: Uri): Screen? {
        val path = uri.path ?: return null
        
        // Try exact match first
        getScreenByRoute(path)?.let { return it }
        
        // Try parameterized route
        val entry = screenRegistry.values
            .filterIsInstance<ScreenRouteEntry.Parameterized>()
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
    ): Screen? {
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
            
            constructor.callBy(args) as? Screen
        } catch (e: Exception) {
            Log.w(TAG, "Failed to create screen from params: ${e.message}")
            null
        }
    }

    /**
     * Checks if a screen belongs to a graph.
     */
    fun isScreenInGraph(screen: Screen, graph: Graph): Boolean {
        if (graph.contains(screen)) return true
        return graphRegistry.values
            .filter { it.isModal }
            .any { it.contains(screen) }
    }

    /**
     * Gets all registered graphs.
     */
    fun getAllGraphs(): Set<Graph> = graphRegistry.values.toSet()

    /**
     * Gets all screens for a specific graph.
     */
    fun getScreensForGraph(graph: Graph): List<Screen> {
        return screenRegistry.values
            .filterIsInstance<ScreenRouteEntry.Singleton>()
            .map { it.screen }
            .filter { graph.contains(it) }
    }

    sealed interface ScreenRouteEntry {
        data class Singleton(val screen: Screen) : ScreenRouteEntry
        data class Parameterized(val screenClass: Class<out Screen>) : ScreenRouteEntry {
            val baseRoute: String = "/" + screenClass.simpleName.lowercase()
        }
    }

    private companion object {
        const val TAG = "RouteRegistry"
    }
}

