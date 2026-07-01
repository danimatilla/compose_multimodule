package com.dxmxp.ui.navigation.helpers

import android.net.Uri
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.Screen
import javax.inject.Inject
import kotlin.reflect.full.primaryConstructor

class DeepLinkHandler @Inject constructor(
    private val graphs: Set<@JvmSuppressWildcards Graph>
) {

    fun handle(uri: Uri): NavigationHandler.NavigationEvent? {
        val path = uri.path ?: return null
        
        // Try exact match first (objects)
        routesMap[path]?.let { return NavigationHandler.NavigationEvent.PushScreen(it) }

        // Try to match with parameters
        val entry = parameterizedRoutes.entries.find { path.startsWith(it.key) } ?: return null
        val screenClass = entry.value
        
        return try {
            val queryParams = uri.queryParameterNames.associateWith { uri.getQueryParameter(it) }
            val screen = if (queryParams.isEmpty()) {
                screenClass.getDeclaredConstructor().newInstance()
            } else {
                val constructor = screenClass.kotlin.primaryConstructor ?: return null
                val args = constructor.parameters.associateWith { param ->
                    val value = queryParams[param.name]
                    when (param.type.classifier) {
                        String::class -> value
                        Int::class -> value?.toIntOrNull()
                        else -> null
                    }
                }
                constructor.callBy(args)
            }
            NavigationHandler.NavigationEvent.PushScreen(screen as Screen)
        } catch (_: Exception) {
            null
        }
    }

    private val routesMap: Map<String, Screen> by lazy {
        val registeredScreens = mutableMapOf<String, Screen>()

        fun register(screen: Screen) {
            registeredScreens[screen.route] = screen
            if (screen is Graph) {
                screen.children.forEach { clazz ->
                    try {
                        val instance = clazz.getField("INSTANCE").get(null) as? Screen
                        if (instance != null) register(instance)
                    } catch (_: Exception) {
                        // Probably a data class, skip for objects map
                    }
                }
            }
        }

        graphs.forEach { register(it) }
        registeredScreens
    }

    private val parameterizedRoutes: Map<String, Class<out Screen>> by lazy {
        val registered = mutableMapOf<String, Class<out Screen>>()

        fun register(screen: Screen) {
            if (screen is Graph) {
                screen.children.forEach { clazz ->
                    if (clazz.declaredFields.none { it.name == "INSTANCE" }) {
                        // It's a class (data class likely), get a temporary instance to know its base route
                        try {
                            // This is a hack, in a real app we'd use a better way to map routes to classes
                            val dummyRoute = "/" + clazz.simpleName.lowercase()
                            registered[dummyRoute] = clazz
                        } catch (_: Exception) {}
                    }
                    
                    // Recursive for subgraphs
                    try {
                        val instance = clazz.getField("INSTANCE").get(null) as? Graph
                        if (instance != null) register(instance)
                    } catch (_: Exception) {}
                }
            }
        }

        graphs.forEach { register(it) }
        registered
    }
}
