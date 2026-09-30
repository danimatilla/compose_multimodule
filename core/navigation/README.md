# Core Navigation Module

> **Type-safe, feature-modular navigation system for Android Compose with centralized deep link resolution and nested graph management.**

A comprehensive, well-structured navigation architecture built on AndroidX Navigation 3 and Compose, providing type-safe route management, dynamic deep linking, automatic nested graph backstack composition, and modular graph isolation.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Core Concepts](#core-concepts)
- [Deep Link Management](#deep-link-management)
- [Nested Graphs & Sub-routes](#nested-graphs--sub-routes)
- [Usage Guide](#usage-guide)
- [Examples](#examples)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

---

## Overview

The **core:navigation** module provides the foundation for type-safe, modular navigation in your Android app. Instead of relying on string-based route matching, this system uses typed `Graph` and `Screen` objects that are composable, serializable, and centrally registered.

### Key Benefits
✅ **Type-safe navigation** - Compiler catches route errors  
✅ **Autonomous Deep Link Router** - Centralized URI → Route → Backstack resolution  
✅ **Automatic Nested Graph Support** - Deep links directly to nested screens render full parent scaffolds and backstacks  
✅ **Modular architecture** - Features define their own graphs without knowledge of the main app  
✅ **Polymorphic Modal Presentation** - `isModal = true` handles slide-up animations and bottom bar visibility  
✅ **Centralized registry** - Single source of truth for all routes  
✅ **Dynamic routing** - Support for parameterized routes  
✅ **ViewModel injection** - Automatic typed ViewModel provisioning (`InitializableViewModel`)  

---

## Features

### Static Routes (Parameter-less)
Routes without parameters that have a single, known instance.

```kotlin
@Serializable
data object Home : Screen {
    override val route: String = "/main/home"
}
```

### Dynamic Routes (Parameterized)
Routes with parameters that create new instances on each navigation.

```kotlin
@Serializable
data class StoryDetail(val id: String) : Screen {
    override val route: String = "/stories/detail"
}
```

### Modal Routes
Routes or Graphs that present as overlays/dialogs with slide-up transitions.

```kotlin
@Serializable
data object Search : Screen {
    override val route: String = "/main/search"
    override val isModal: Boolean get() = true
}
```

### Nested Graph Composition with `initialRoute`
Graphs are collections of screens that define a navigation context. They can accept an optional `initialRoute` when navigating to a deep sub-screen from a deep link.

```kotlin
@Serializable
data class StoriesGraph(
    override val initialRoute: Route? = null
) : Graph {
    override val isModal: Boolean get() = true
    override val route: String get() = "/stories"

    override fun withInitialRoute(route: Route): Graph = copy(initialRoute = route)
}
```

---

## Architecture

### Component Hierarchy

```
┌─────────────────────────────────────────────┐
│  AppGraphsModule (in :seed app module)      │ ← Central registration
│  • Provides Set<Graph>                      │
│  • Includes: MainGraph, AuthGraph, etc.     │
└────────────────┬────────────────────────────┘
                 │ (injected via Hilt)
                 ↓
┌─────────────────────────────────────────────┐
│  RouteRegistry (this module, Singleton)     │ ← Central route lookup
│  ├── routeMap: Map<RouteKey, Route>         │
│  ├── pathMap: Map<String, Route>            │
│  └── routeCreators: Map<...>                │
└────────────────┬────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────────────┐
│  DeepLinkRouter (this module, Singleton)    │ ← Central Deep Link processing
│  ├── Resolves URI → Route                   │
│  ├── Checks requiresAuth requirement        │
│  └── Builds atomic NavAction.UpdateStack    │
└─────────────────────────────────────────────┘
```

### Navigation Data Flow

#### Internal Navigation
```
viewModel.setEvent(OnRouteClicked(route))
    ↓
deepLinkRouter.process(route, MainGraph)
    ↓
NavAction.UpdateStack emitted
    ↓
navigator.navAction(action) updates NavBackStack
```

#### Deep Link Navigation
```
URI: app://stories/detail?id=123
    ↓
DeepLinkRouter.handle(uri, mainGraph, authGraph, isAuthenticated)
    ↓
1. URI mapped to StoriesGraph.StoryDetail(id="123")
2. Authentication verified
3. Graph configured: StoriesGraph(initialRoute = StoryDetail("123"))
4. Action created: root(MainGraph) -> push(StoriesGraph(initialRoute = StoryDetail("123")))
    ↓
MainScaffold renders → StoriesScaffold renders → StoryDetailScreen rendered over Home
```

---

## Core Concepts

### `Route` Interface
Base interface for all navigation keys. Must be `@Serializable`.

```kotlin
interface Route : NavKey {
    val route: String
    val isModal: Boolean get() = false
    val showMainBottomBar: Boolean get() = !isModal
    val requiresAuth: Boolean get() = true
}
```

### `Graph` Interface
Collection of related screens with a common navigation context.

```kotlin
interface Graph : Route {
    val initialRoute: Route? get() = null
    fun withInitialRoute(route: Route): Graph = this

    fun EntryProviderScope<NavKey>.registerScreens()
    fun staticRoutes(): Set<RouteRegistration>
    fun dynamicRoutePatterns(): Map<String, (Uri) -> Route?>
}
```

### `Screen` Interface
Individual screens within a graph.

```kotlin
interface Screen : Route {
    companion object {
        inline fun <reified K : Route> EntryProviderScope<NavKey>.screenEntry(
            metadata: Map<String, Any> = emptyMap(),
            crossinline content: @Composable (K) -> Unit,
        ) {
            // Screen entry implementation
        }
    }
}
```

### `Navigator` & `NavAction`
Encapsulates `NavBackStack<NavKey>` and provides atomic stack updates via `navAction()` and DSL `updateStack`.

```kotlin
class Navigator(private val backStack: NavBackStack<NavKey>) {
    fun push(route: Route)
    fun pop()
    fun popTo(route: Route)
    fun root(route: Route)
    fun updateStack(block: StackBuilder.() -> Unit)
    fun navAction(action: NavAction)
}

sealed interface NavAction {
    data class Push(val route: Route) : NavAction
    data object Pop : NavAction
    data class PopTo(val route: Route) : NavAction
    data class Root(val route: Route) : NavAction
    data class UpdateStack(val block: Navigator.StackBuilder.() -> Unit) : NavAction
}
```

---

## Deep Link Management

Deep link resolution is handled centrally by **`DeepLinkRouter`** inside `:core:navigation`.

```kotlin
@Singleton
class DeepLinkRouter @Inject constructor(
    private val routeRegistry: RouteRegistry,
) {
    fun resolve(uri: Uri): Route?

    fun process(route: Route, mainGraph: Graph): NavAction

    fun handle(
        uri: Uri,
        mainGraph: Graph,
        authGraph: Graph? = null,
        isAuthenticated: Boolean = true,
    ): DeepLinkResult
}
```

### Using DeepLinkRouter in ViewModels

```kotlin
private fun handleDeepLink(uri: Uri) {
    viewModelScope.launch {
        val isAuthenticated = hasSessionUseCase(Unit)
        when (val result = deepLinkRouter.handle(uri, mainGraph = MainGraph, authGraph = AuthGraph, isAuthenticated = isAuthenticated)) {
            is DeepLinkResult.Success -> {
                setEffect { Effect.Navigate(result.action) }
            }
            is DeepLinkResult.RequiresAuth -> {
                setState { copy(pendingRoute = result.pendingRoute) }
                result.action?.let { action ->
                    setEffect { Effect.Navigate(action) }
                }
            }
            is DeepLinkResult.Unresolved -> {
                // Ignore or log
            }
        }
    }
}
```

---

## Nested Graphs & Sub-routes

When a deep link targets a screen inside a nested feature graph (e.g. `StoryDetail` inside `StoriesGraph`), the parent graph receives the target route via `initialRoute`.

### Scaffold Implementation

```kotlin
@Composable
fun StoriesScaffold(
    initialRoute: Route? = null,
    rootNavigator: Navigator = LocalNavigator.current,
) {
    val initialStack: List<Route> = remember(initialRoute) {
        if (initialRoute != null && initialRoute != StoriesGraph.Home) {
            listOf(StoriesGraph.Home, initialRoute)
        } else {
            listOf(StoriesGraph.Home)
        }
    }

    val nestedBackStack = rememberNavBackStack(*initialStack.toTypedArray())
    val nestedNavigator = rememberNavigator(nestedBackStack)

    CompositionLocalProvider(LocalNavigator provides nestedNavigator) {
        Scaffold(
            // Scaffold content
        ) { innerPadding ->
            StoriesNavDisplay(
                backStack = nestedBackStack,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
```

---

## Usage Guide

### 1. Define a Feature Graph

```kotlin
@Serializable
data class StoriesGraph(
    override val initialRoute: Route? = null
) : Graph {
    override val isModal: Boolean get() = true
    override val route: String get() = "/stories"

    override fun withInitialRoute(route: Route): Graph = copy(initialRoute = route)

    @Serializable
    data object Home : Screen {
        override val route: String get() = "/stories/home"
    }

    @Serializable
    data class StoryDetail(val id: String) : Screen {
        override val route: String get() = "/stories/detail"
    }

    override fun staticRoutes() = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(Home.route), Home),
    )

    override fun dynamicRoutePatterns() = mapOf(
        "/stories/detail" to { uri ->
            val id = uri.getQueryParameter("id") ?: return@mapOf null
            StoryDetail(id = id)
        }
    )

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<StoriesGraph> { graph ->
            StoriesScaffold(initialRoute = graph.initialRoute)
        }
        screenEntry<Home> { HomeScreen() }
        screenEntry<StoryDetail, StoryDetailViewModel>(
            viewModelProvide = { hiltViewModel() }
        ) { vm -> StoryDetailScreen(vm) }
    }
}
```

### 2. Register in `AppGraphsModule`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppGraphsModule {

    @Provides
    @IntoSet
    fun provideStoriesGraph(): Graph = StoriesGraph()
}
```

---

## Best Practices

### ✅ DO

- **Keep `:core:navigation` completely feature-agnostic** - No hardcoded graph or route strings inside the navigation library.
- **Use `DeepLinkRouter` for URI resolution and backstack construction** - Avoid custom URI parsers in Activity or ViewModels.
- **Implement `withInitialRoute` on parameterized graphs** - Enables automatic deep link resolution to sub-screens.
- **Use `NavigationStore` for large payload objects** - Avoid passing large Parcelable/Serializable objects in route arguments.

### ❌ DON'T

- **Hardcode route strings in navigation calls** - Always use typed routes (`navigator.push(StoriesGraph.Home)`).
- **Manually build nested backstacks in UI** - Let `DeepLinkRouter` and `Graph.withInitialRoute` handle graph targets.

---

## Module Structure

```
core/navigation/
├── src/main/java/com/dxmxp/navigation/
│   ├── core/
│   │   ├── Navigator.kt           # Back stack wrapper & DSL
│   │   ├── NavigationStore.kt     # Large data cache singleton
│   │   └── RouteRegistry.kt       # Central route lookup & registry
│   ├── model/
│   │   ├── Route.kt               # Base interface
│   │   ├── Graph.kt               # Graph interface with initialRoute
│   │   ├── Screen.kt              # Screen helper & entry builders
│   │   └── RouteKey.kt            # Type-safe key wrapper
│   ├── di/
│   │   └── NavigationModule.kt    # Hilt configuration
│   ├── utils/
│   │   ├── DeepLinkRouter.kt      # Autonomous Deep Link router & result models
│   │   └── NavigationUtils.kt     # Animation & UI helpers
│   └── common/
│       └── InitializableViewModel.kt # ViewModel init contract
└── build.gradle.kts
```

---

**Maintained By:** Navigation Architecture Team  
**License:** Apache 2.0
