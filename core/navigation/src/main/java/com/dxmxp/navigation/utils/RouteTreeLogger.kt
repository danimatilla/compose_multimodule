package com.dxmxp.navigation.utils

import android.util.Log
import com.dxmxp.navigation.core.RouteRegistry
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route

/**
 * Standalone utility to build and log the hierarchical route tree and nested graphs.
 * Designed generically for archetypes without hardcoded graph names or route paths.
 */
object RouteTreeLogger {

    private const val TAG = "RouteTreeLogger"

    /**
     * Logs the route tree from a [RouteRegistry].
     *
     * @param routeRegistry Central route registry.
     * @param logPrinter Optional logging lambda (e.g., `{ tag, msg -> logger.d(tag, msg) }`).
     *                   If null, falls back to Android's [Log.d] or [println].
     * @param rootGraphs Optional custom root graphs.
     */
    fun log(
        routeRegistry: RouteRegistry,
        logPrinter: ((tag: String, message: String) -> Unit)? = null,
        rootGraphs: Set<Graph>? = null,
    ) {
        val graphs = routeRegistry.extractGraphs()
        log(graphs, logPrinter, rootGraphs)
    }

    /**
     * Logs the route tree from a collection of [Graph].
     */
    fun log(
        graphs: Set<Graph>,
        logPrinter: ((tag: String, message: String) -> Unit)? = null,
        rootGraphs: Set<Graph>? = null,
    ) {
        val treeString = buildTreeString(graphs, rootGraphs)
        if (logPrinter != null) {
            logPrinter(TAG, treeString)
        } else {
            try {
                Log.d(TAG, treeString)
            } catch (_: Throwable) {
                println("[$TAG]\n$treeString")
            }
        }
    }

    /**
     * Generates the String representation of the route tree.
     */
    fun buildTreeString(
        graphs: Set<Graph>,
        customRootGraphs: Set<Graph>? = null,
    ): String {
        if (graphs.isEmpty()) return "Route Tree: [Empty]"

        val roots = customRootGraphs ?: autoDetectRoots(graphs)
        val availableNested = graphs - roots

        val sb = StringBuilder()
        sb.append("\nNavigation Route Tree:\n")
        sb.append("======================\n")

        val rootList = roots.toList()
        rootList.forEachIndexed { index, rootGraph ->
            val isLastRoot = index == rootList.size - 1
            val prefix = if (isLastRoot) "└── " else "├── "
            val childIndent = if (isLastRoot) "    " else "│   "

            formatGraph(sb, rootGraph, prefix, childIndent, availableNested)
        }

        return sb.toString().trimEnd()
    }

    /**
     * Automatically detects root graphs based on authentication requirements and path hierarchy.
     */
    private fun autoDetectRoots(graphs: Set<Graph>): Set<Graph> {
        if (graphs.isEmpty()) return emptySet()

        val pathNested = graphs.filter { child ->
            graphs.any { parent ->
                parent != child && isSubPath(parentRoute = parent.route, childRoute = child.route)
            }
        }.toSet()

        val authGraphs = graphs.filter { it.requiresAuth } - pathNested
        val primaryAuthRoot = authGraphs.firstOrNull()

        val featureNested = if (primaryAuthRoot != null) {
            authGraphs.filter { it != primaryAuthRoot }.toSet()
        } else {
            emptySet()
        }

        val allNested = pathNested + featureNested
        val rootCandidateGraphs = graphs - allNested

        return rootCandidateGraphs.ifEmpty { setOf(graphs.first()) }
    }

    /**
     * Finds graphs nested under [parentGraph] via path hierarchy or feature nesting.
     */
    private fun findNestedGraphs(parentGraph: Graph, availableGraphs: Set<Graph>): Set<Graph> {
        val pathChildren = availableGraphs.filter { child ->
            child != parentGraph && isSubPath(
                parentRoute = parentGraph.route,
                childRoute = child.route
            )
        }.toSet()

        val featureChildren = if (parentGraph.requiresAuth) {
            availableGraphs.filter { child -> child != parentGraph && child.requiresAuth }
        } else {
            emptySet()
        }

        return pathChildren + featureChildren
    }

    /**
     * Checks if [childRoute] is a sub-path of [parentRoute].
     */
    private fun isSubPath(parentRoute: String, childRoute: String): Boolean {
        val normalizedParent = parentRoute.trimEnd('/')
        val normalizedChild = childRoute.trimEnd('/')

        if (normalizedParent.isEmpty() || normalizedParent == "/") {
            return normalizedChild.startsWith("/") && normalizedChild != "/"
        }

        return normalizedChild.startsWith("$normalizedParent/")
    }

    private fun formatGraph(
        sb: StringBuilder,
        graph: Graph,
        prefix: String,
        indent: String,
        availableNestedGraphs: Set<Graph>,
    ) {
        val isModal = Route.isModal(graph)
        val modalTag = if (isModal) " | Modal" else ""
        val botomBarTag = if (!graph.showMainBottomBar) " | ❌ MainBottomBar" else ""
        val graphName = graph::class.java.simpleName.ifBlank { "Graph" }

        sb.append("${prefix}📕 $graphName [${graph.route}] :: ${if (graph.requiresAuth) "🔒" else "🔓"}$botomBarTag$modalTag\n")

        val staticScreens = graph.staticRoutes()
            .map { it.route }
            .filter { it != graph && it::class.java != graph::class.java }

        val dynamicPatterns = graph.dynamicRoutePatterns().keys.toList()

        val nestedForThisGraph = findNestedGraphs(graph, availableNestedGraphs)
        val remainingNestedGraphs = availableNestedGraphs - nestedForThisGraph

        val totalChildrenCount = staticScreens.size + dynamicPatterns.size + nestedForThisGraph.size
        var childIndex = 0

        childIndex = appendStaticScreens(sb, staticScreens, indent, childIndex, totalChildrenCount)
        childIndex =
            appendDynamicPatterns(sb, dynamicPatterns, indent, childIndex, totalChildrenCount)
        appendNestedGraphs(
            sb,
            nestedForThisGraph,
            remainingNestedGraphs,
            indent,
            childIndex,
            totalChildrenCount
        )
    }

    private fun appendStaticScreens(
        sb: StringBuilder,
        staticScreens: List<Route>,
        indent: String,
        startIndex: Int,
        totalChildrenCount: Int,
    ): Int {
        var currentIndex = startIndex
        staticScreens.forEach { screen ->
            currentIndex++
            val isLastChild = currentIndex == totalChildrenCount
            val screenPrefix = if (isLastChild) "└── " else "├── "
            val screenName = screen::class.java.simpleName.ifBlank { "Screen" }
            val screenModalTag = if (Route.isModal(screen)) " :: Modal" else ""

            sb.append("$indent$screenPrefix📃 $screenName [${screen.route}]$screenModalTag\n")
        }
        return currentIndex
    }

    private fun appendDynamicPatterns(
        sb: StringBuilder,
        dynamicPatterns: List<String>,
        indent: String,
        startIndex: Int,
        totalChildrenCount: Int,
    ): Int {
        var currentIndex = startIndex
        dynamicPatterns.forEach { pattern ->
            currentIndex++
            val isLastChild = currentIndex == totalChildrenCount
            val patternPrefix = if (isLastChild) "└── " else "├── "

            sb.append("$indent$patternPrefix📜 Pattern: $pattern\n")
        }
        return currentIndex
    }

    private fun appendNestedGraphs(
        sb: StringBuilder,
        nestedForThisGraph: Set<Graph>,
        remainingNestedGraphs: Set<Graph>,
        indent: String,
        startIndex: Int,
        totalChildrenCount: Int,
    ) {
        var currentIndex = startIndex
        val nestedList = nestedForThisGraph.toList()

        nestedList.forEach { nestedGraph ->
            currentIndex++
            val isLastChild = currentIndex == totalChildrenCount
            val nestedPrefix = if (isLastChild) "└── " else "├── "
            val childNestedIndent = if (isLastChild) "    " else "│   "

            formatGraph(
                sb = sb,
                graph = nestedGraph,
                prefix = "$indent$nestedPrefix",
                indent = "$indent$childNestedIndent",
                availableNestedGraphs = remainingNestedGraphs
            )
        }
    }

    private fun RouteRegistry.extractGraphs(): Set<Graph> {
        return try {
            val field = RouteRegistry::class.java.getDeclaredField("graphs")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            (field.get(this) as? Set<Graph>) ?: emptySet()
        } catch (_: Exception) {
            emptySet()
        }
    }
}

/** Public Extensions **/

fun RouteRegistry.logRouteTree(
    logPrinter: ((tag: String, message: String) -> Unit)? = null,
    rootGraphs: Set<Graph>? = null,
) {
    RouteTreeLogger.log(this, logPrinter, rootGraphs)
}

fun Set<Graph>.logRouteTree(
    logPrinter: ((tag: String, message: String) -> Unit)? = null,
    rootGraphs: Set<Graph>? = null,
) {
    RouteTreeLogger.log(this, logPrinter, rootGraphs)
}
