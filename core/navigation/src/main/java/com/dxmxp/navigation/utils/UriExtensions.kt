package com.dxmxp.navigation.utils

import android.net.Uri
import com.dxmxp.navigation.model.Route
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

/**
 * Dynamically parses query parameters from a [Uri] using reflection and instantiates a [Route] of type [T].
 *
 * It inspects the primary constructor of [T] and maps each constructor parameter name
 * to the corresponding query parameter in the [Uri].
 *
 * Supported parameter types:
 * - [String]
 * - [Int] (parsed via [String.toIntOrNull])
 * - [Long] (parsed via [String.toLongOrNull])
 * - [Boolean] (parsed via [String.toBooleanStrictOrNull])
 * - Optional parameters (default constructor values are preserved if missing in URI)
 *
 * @return An instance of [T] if all required parameters are present and valid, or `null` otherwise.
 *
 * Example:
 * ```
 * @Serializable
 * data class StoryDetail(val id: String, val page: Int = 1) : Screen
 *
 * val uri = Uri.parse("app://stories/detail?id=123&page=2")
 * val route: StoryDetail? = uri.parseRoute<StoryDetail>()
 * ```
 */
inline fun <reified T : Route> Uri.parseRoute(): T? {
    val kClass = T::class
    val primaryConstructor = kClass.primaryConstructor ?: return null
    val args = mutableMapOf<KParameter, Any?>()

    for (param in primaryConstructor.parameters) {
        val paramName = param.name ?: continue
        val queryValue = getQueryParameter(paramName)?.takeIf { it.isNotBlank() }

        if (queryValue != null) {
            val convertedValue: Any = when (param.type.classifier) {
                String::class -> queryValue
                Int::class -> queryValue.toIntOrNull() ?: return null
                Long::class -> queryValue.toLongOrNull() ?: return null
                Boolean::class -> queryValue.toBooleanStrictOrNull() ?: return null
                else -> queryValue
            }
            args[param] = convertedValue
        } else if (!param.isOptional) {
            return null
        }
    }

    return try {
        primaryConstructor.callBy(args)
    } catch (_: Exception) {
        null
    }
}

/**
 * DSL helper to register dynamic route patterns in a [com.dxmxp.navigation.model.Graph].
 *
 * Maps a path string pattern to a constructor function that parses the incoming [Uri]
 * into a type-safe [Route] of type [T] using [Uri.parseRoute].
 *
 * Example:
 * ```
 * override fun dynamicRoutePatterns(): Map<String, (Uri) -> Route?> = mapOf(
 *     routePattern<StoryDetail>(STORIES_DETAIL_PATH),
 * )
 * ```
 *
 * @param path The base path pattern (e.g., `"/stories/detail"`).
 * @return A [Pair] containing the path pattern and the URI-to-Route factory lambda.
 */
inline fun <reified T : Route> routePattern(
    path: String,
): Pair<String, (Uri) -> Route?> = path to { uri -> uri.parseRoute<T>() }
