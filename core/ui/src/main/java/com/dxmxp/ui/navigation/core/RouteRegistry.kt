package com.dxmxp.ui.navigation.core

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.dxmxp.domain.base.Logger
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
    graphs: Set<@JvmSuppressWildcards Graph>,
    private val logger: Logger
) {
    private val routeRegistry = mutableMapOf<String, RouteEntry>()
    private val graphRegistry = mutableMapOf<String, Graph>()
    private val processedGraphs = mutableSetOf<String>()
    
    init {
        logger.d(TAG, "🚀 RouteRegistry: Initializing registration...")
        
        // Start registration from top-level graphs only to ensure proper nesting in logs
        val roots = graphs.filter { graph ->
            graphs.none { other -> 
                other !== graph && other.screens.any { it.isAssignableFrom(graph.javaClass) }
            }
        }

        roots.forEach { registerGraph(it) }
        
        // Catch any remaining graphs (orphans or circular refs)
        graphs.forEach { registerGraph(it) }
        
        logger.d(TAG, "🏁 RouteRegistry: Ready with ${routeRegistry.size} routes and ${graphRegistry.size} graphs")
    }

    /**
     * Registers a graph and all its child screens.
     */
    private fun registerGraph(graph: Graph, depth: Int = 0) {
        if (processedGraphs.contains(graph.route)) return
        
        val indent = "  ".repeat(depth)
        logger.d(TAG, "$indent📍 Graph: ${graph.route}")
        
        graphRegistry[graph.route] = graph
        processedGraphs.add(graph.route)
        routeRegistry[graph.route] = RouteEntry.Singleton(graph)
        
        graph.screens.forEach { routeClass ->
            if (routeClass.isAssignableFrom(graph.javaClass)) return@forEach

            try {
                // Try to get singleton instance (data object)
                val instance = try {
                    routeClass.getField("INSTANCE").get(null) as? Route
                } catch (_: Exception) {
                    null
                }

                if (instance != null) {
                    if (instance is Graph) {
                        registerGraph(instance, depth + 1)
                    } else {
                        routeRegistry[instance.route] = RouteEntry.Singleton(instance)
                        logger.d(TAG, "$indent ├─ 📄 [S] ${instance.route}")
                    }
                } else {
                    // It's a parameterized route (data class)
                    // We check if it's a Screen class
                    @Suppress("UNCHECKED_CAST")
                    if (Screen::class.java.isAssignableFrom(routeClass)) {
                        registerParameterizedScreen(routeClass as Class<out Screen>, depth)
                    }
                }
            } catch (e: Exception) {
                logger.w(TAG, "$indent ├─ ❌ Failed to register ${routeClass.simpleName}: ${e.message}")
            }
        }
    }

    /**
     * Registers a screen that accepts parameters.
     */
    private fun registerParameterizedScreen(screenClass: Class<out Screen>, depth: Int) {
        val indent = "  ".repeat(depth)
        val baseRoute = Route.calculateRoute(screenClass)
        routeRegistry[baseRoute] = RouteEntry.Parameterized(screenClass, baseRoute)
        logger.d(TAG, "$indent ├─ 📦 [P] $baseRoute (${screenClass.simpleName})")
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
        val path = uri.path?.removeSuffix("/") ?: return null
        
        // Try exact match first
        getRouteByRoute(path)?.let { 
            logger.d(TAG, "🔗 Deep Link Match [Exact]: $path")
            return it 
        }
        
        // Try parameterized route
        val entry = routeRegistry.values
            .filterIsInstance<RouteEntry.Parameterized>()
            .firstOrNull { 
                path == it.baseRoute || path.startsWith("${it.baseRoute}/") || 
                (it.baseRoute.isNotEmpty() && path.startsWith(it.baseRoute) && path.length > it.baseRoute.length && path[it.baseRoute.length] == '?')
            }
        
        if (entry == null) {
            logger.w(TAG, "⚠️ Deep Link No Match: $path")
            return null
        }

        logger.d(TAG, "🔗 Deep Link Match [Param]: $path -> ${entry.screenClass.simpleName}")
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

            val constructor = screenClass.kotlin.primaryConstructor ?: run {
                logger.w(TAG, "   └─ ❌ No primary constructor found for ${screenClass.simpleName}")
                return null
            }
            val args = constructor.parameters
                .filter { queryParams.containsKey(it.name) || !it.isOptional }
                .associateWith { param ->
                    val paramValue = queryParams[param.name]
                    if (paramValue == null) {
                        if (param.isOptional) return@associateWith null
                        else {
                            logger.w(TAG, "   └─ ❌ Missing mandatory param: ${param.name}")
                            return null
                        }
                    }
                    
                    val parsedValue = when (param.type.classifier) {
                        String::class -> paramValue
                        Int::class -> paramValue.toIntOrNull()
                        Boolean::class -> paramValue.toBoolean()
                        Long::class -> paramValue.toLongOrNull()
                        else -> {
                            logger.w(TAG, "   └─ ❌ Unsupported param type: ${param.type.classifier}")
                            null
                        }
                    }
                    
                    if (parsedValue == null && !param.type.isMarkedNullable) {
                        logger.w(TAG, "   └─ ❌ Failed to parse param: ${param.name} = $paramValue")
                        return null
                    }
                    parsedValue
                }
            
            logger.d(TAG, "   └─ Params: $queryParams")
            val filteredArgs = args.filter { (param, value) -> value != null || param.type.isMarkedNullable }
            constructor.callBy(filteredArgs as Map<kotlin.reflect.KParameter, Any?>) as? Route
        } catch (e: Exception) {
            logger.w(TAG, "   └─ ❌ Error creating screen ${screenClass.simpleName}: ${e.message}")
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
        data class Parameterized(val screenClass: Class<out Screen>, val baseRoute: String) : RouteEntry
    }

    private companion object {
        const val TAG = "RouteRegistry"
    }
}

