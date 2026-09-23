# Core Navigation Module

> **Type-safe, feature-modular navigation system for Android Compose with centralized deep link resolution.**

A comprehensive, well-structured navigation architecture built on Androidx Navigation 3 and Compose, providing type-safe route management, dynamic deep linking, and modular graph composition.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Core Concepts](#core-concepts)
- [Usage Guide](#usage-guide)
- [Examples](#examples)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

---

## Overview

The **core:navigation** module provides the foundation for type-safe, modular navigation in your Android app. Instead of relying on string-based route matching, this system uses typed `Graph` and `Screen` objects that are composable, serializable, and centrally registered.

### Key Benefits
✅ **Type-safe navigation** - Compiler catches route errors  
✅ **Deep link support** - Automatic URI → Route resolution  
✅ **Modular architecture** - Features define their own graphs  
✅ **Centralized registry** - Single source of truth for all routes  
✅ **Dynamic routing** - Support for parameterized routes  
✅ **ViewModel injection** - Automatic typed ViewModel provisioning  

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

**Resolution:** Type-safe lookup or deep link matching

### Dynamic Routes (Parameterized)
Routes with parameters that create new instances on each navigation.

```kotlin
@Serializable
data class StoryDetail(val id: String) : Screen {
    override val route: String = "/stories/detail"
}
```

**Resolution:** URI parsing with query parameter extraction

### Graph Composition
Graphs are collections of screens that define a navigation context.

```kotlin
@Serializable
data object StoriesGraph : Graph {
    override val route: String = "/stories"
    // ... screens ...
    override fun staticRoutes() = setOf(...)
    override fun dynamicRoutePatterns() = mapOf(...)
}
```

### Deep Linking
Incoming URIs like `app://stories/detail?id=123` are automatically resolved to route instances.

### ViewModel Injection
Screen entries can automatically inject typed ViewModels.

```kotlin
screenEntry<StoryDetail, StoryDetailViewModel>(
    viewModelProvide = { hiltViewModel() }
) { viewModel -> StoryDetailScreen(viewModel) }
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
│  RouteRegistry (this module, Singleton)     │ ← Central lookup
│  ├── routeMap: Map<RouteKey, Route>         │
│  ├── pathMap: Map<String, Route>            │
│  └── routeCreators: Map<...>                │
└────────────────┬────────────────────────────┘
                 │
        ┌────────┴────────┐
        ↓                 ↓
   Type-safe      Deep Link
   Lookup         Resolution
   (Internal)     (External)
```

### Data Flow

#### Internal Navigation (Type-safe)
```
navigator.push(MainGraph.Home)
    ↓
Route instance pushed to back stack
    ↓
Navigation engine renders screen
```

#### Deep Link Navigation
```
URI: app://stories/detail?id=123
    ↓
RouteRegistry.createRouteFromUri()
    ↓
Pattern match + parameter extraction
    ↓
StoryDetail(id="123") created
    ↓
Navigation engine renders screen
```

---

## Core Concepts

### `Route` Interface
Base interface for all navigation keys. Must be `@Serializable`.

```kotlin
interface Route : NavKey {
    val route: String
    val showMainBottomBar: Boolean get() = true
    val requiresAuth: Boolean get() = true
}
```

### `Graph` Interface
Collection of related screens with a common navigation context.

**Responsibilities:**
- Define screen hierarchy: `registerScreens()`
- Expose static routes: `staticRoutes()`
- Expose dynamic patterns: `dynamicRoutePatterns()`

```kotlin
interface Graph : Route {
    fun EntryProviderScope<NavKey>.registerScreens()
    fun staticRoutes(): Set<RouteRegistration> = ...
    fun dynamicRoutePatterns(): Map<String, (Uri) -> Route?> = ...
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
        ) { }
    }
}
```

### `RouteKey`
Type-safe wrapper for route paths. Prevents passing arbitrary strings.

```kotlin
@JvmInline
value class RouteKey private constructor(val value: String)

// Usage
val key = RouteKey.of("/stories")
val route = registry.getRoute(key)  // Type-safe!
```

### `RouteRegistry`
Central singleton for route lookup and deep link resolution.

```kotlin
@Singleton
class RouteRegistry @Inject constructor(
    private val graphs: Set<@JvmSuppressWildcards Graph>
) {
    fun getRoute(key: RouteKey): Route?
    fun getRoute(path: String): Route?
    fun createRouteFromUri(uri: Uri): Route?
    fun register(pattern: String, creator: (Uri) -> Route?)
}
```

### `NavigationStore`
Singleton cache for large data between screens (avoids `TransactionTooLargeException`).

```kotlin
@Singleton
class NavigationStore @Inject constructor() {
    fun <T> pushData(key: String, data: T)
    fun <T> getData(key: String): T?
    fun <T> observeResult(key: String): Flow<T>
    fun emitResult(key: String, data: Any)
}
```

---

## Usage Guide

### 1. Define a Graph

```kotlin
@Serializable
data object StoryGraph : Graph {
    override val route: String = "/stories"
    override val isModal: Boolean = true

    @Serializable
    data object Feed : Screen {
        override val route: String = "/stories/feed"
    }

    @Serializable
    data class Detail(val id: String) : Screen {
        override val route: String = "/stories/detail"
    }

    override fun staticRoutes() = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(Feed.route), Feed),
    )

    override fun dynamicRoutePatterns() = mapOf(
        "/stories/detail" to { uri ->
            val id = uri.getQueryParameter("id") ?: return@mapOf null
            Detail(id = id)
        }
    )

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<StoryGraph> { StoriesScaffold() }
        screenEntry<Feed> { FeedScreen() }
        screenEntry<Detail, DetailViewModel>(
            viewModelProvide = { hiltViewModel() }
        ) { vm -> DetailScreen(vm) }
    }
}
```

### 2. Register in AppGraphsModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppGraphsModule {
    
    @Provides
    @IntoSet
    fun provideStoriesGraph(): Graph = StoriesGraph
}
```

### 3. Use in Navigation

```kotlin
// Type-safe navigation (recommended)
val navigator = LocalNavigator.current
navigator.push(StoriesGraph.Feed)
navigator.push(StoriesGraph.Detail(id = "story_123"))

// Atomic stack updates via DSL
navigator.updateStack {
    root(MainGraph.Home)
    push(StoriesGraph.Detail(id = "story_123"))
}

// Effect-based navigation via NavAction
LaunchedEffect(Unit) {
    viewModel.effect.collect(navigator::navAction)
}

// Deep links (automatic)
// app://stories/feed → StoriesGraph.Feed
// app://stories/detail?id=story_123 → StoriesGraph.Detail(id="story_123")
```

---

## Examples

### Example 1: Simple Graph

```kotlin
@Serializable
data object SettingsGraph : Graph {
    override val route: String = "/settings"

    @Serializable
    data object Account : Screen {
        override val route: String = "/settings/account"
    }

    @Serializable
    data object Privacy : Screen {
        override val route: String = "/settings/privacy"
    }

    override fun staticRoutes() = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(Account.route), Account),
        RouteRegistration(RouteKey.of(Privacy.route), Privacy),
    )

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<SettingsGraph> { SettingsScaffold() }
        screenEntry<Account> { AccountScreen() }
        screenEntry<Privacy> { PrivacyScreen() }
    }
}
```

### Example 2: Graph with Dynamic Routes

```kotlin
@Serializable
data object ProductGraph : Graph {
    override val route: String = "/products"

    @Serializable
    data object List : Screen {
        override val route: String = "/products/list"
    }

    @Serializable
    data class Detail(val productId: String, val category: String? = null) : Screen {
        override val route: String = "/products/detail"
    }

    override fun staticRoutes() = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(List.route), List),
    )

    override fun dynamicRoutePatterns() = mapOf(
        "/products/detail" to { uri ->
            val id = uri.getQueryParameter("productId") ?: return@mapOf null
            val category = uri.getQueryParameter("category")
            Detail(productId = id, category = category)
        }
    )

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<ProductGraph> { ProductScaffold() }
        screenEntry<List> { ProductListScreen() }
        screenEntry<Detail, ProductViewModel>(
            viewModelProvide = { hiltViewModel() }
        ) { vm -> ProductDetailScreen(vm) }
    }
}
```

### Example 3: Passing Large Data

Instead of passing large objects through routes, use `NavigationStore`:

```kotlin
// In source screen
val navigationStore: NavigationStore by inject()
val user = User(id = "123", name = "John", ...) // Large object
navigationStore.pushData("user_detail_payload", user)
navigator.push(UserDetail(userId = "123"))

// In destination ViewModel
val user: User? = navigationStore.getData("user_detail_payload")
```

### Example 4: Cross-Module Navigation

```kotlin
// Get a route from another module
val registry: RouteRegistry by inject()
val profileRoute = registry.getRoute(RouteKey.of("/profile"))
if (profileRoute != null) {
    navigator.push(profileRoute)
}
```

---

## Best Practices

### ✅ DO

- **Use static routes for parameter-less screens**
  ```kotlin
  @Serializable
  data object Home : Screen { ... }
  ```

- **Use dynamic patterns for parameterized routes**
  ```kotlin
  override fun dynamicRoutePatterns() = mapOf(
      "/detail" to { uri -> Detail(id = uri.getQueryParameter("id")) }
  )
  ```

- **Use `NavigationStore` for large data**
  ```kotlin
  navigationStore.pushData("key", largeObject)
  ```

- **Validate deep link parameters**
  ```kotlin
  val id = uri.getQueryParameter("id") ?: return@mapOf null
  ```

- **Keep graphs focused** - One graph per navigation context

### ❌ DON'T

- **Pass large objects through routes**
  ```kotlin
  // ❌ This can cause TransactionTooLargeException
  data class Detail(val user: User) : Screen
  
  // ✅ Instead, use NavigationStore
  ```

- **Use unbounded strings for routes**
  ```kotlin
  // ❌ Error-prone
  navigator.push("/stories/detail")
  
  // ✅ Type-safe
  navigator.push(StoriesGraph.Detail(id = "123"))
  ```

- **Duplicate route definitions**
  ```kotlin
  // ❌ Register routes in multiple modules
  
  // ✅ Centralize in AppGraphsModule
  ```

- **Mix serialization formats**
  ```kotlin
  // Keep all routes and screens @Serializable
  ```

---

## Troubleshooting

### Deep link not resolving

**Issue:** `RouteRegistry.createRouteFromUri()` returns null

**Solutions:**
1. Check path normalization: `app://stories//detail` → `/stories/detail`
2. Ensure patterns are registered in `dynamicRoutePatterns()`
3. Use exact path matching, not partial (`"/stories/detail"` not `"/stories"`)
4. Verify query parameters are extracted correctly

```kotlin
// Debug
val uri = Uri.parse("app://stories/detail?id=123")
val resolved = registry.createRouteFromUri(uri)
Log.d("Navigation", "Resolved: $resolved")
```

### ViewModel not injected

**Issue:** "Route has parameters but ViewModel does not implement InitializableViewModel"

**Solution:** Ensure ViewModel implements the contract:

```kotlin
class DetailViewModel : ViewModel(), InitializableViewModel<StoriesGraph.Detail> {
    override fun init(route: StoriesGraph.Detail) {
        val id = route.id
        // Load data using id
    }
}
```

### `Set<Graph>` not provided by Hilt

**Issue:** `java.util.Set<? extends com.dxmxp.navigation.model.Graph> cannot be provided`

**Solution:** Add `@JvmSuppressWildcards` in `RouteRegistry`:

```kotlin
class RouteRegistry @Inject constructor(
    private val graphs: Set<@JvmSuppressWildcards Graph>,
)
```

### Graphs not registered

**Issue:** Routes from certain graphs not found

**Solution:** Verify graphs are provided in `AppGraphsModule`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppGraphsModule {
    @Provides @IntoSet
    fun provideMyGraph(): Graph = MyGraph  // ← Must be present
}
```

---

## Integration Checklist

- [ ] Core navigation module added to project
- [ ] `Graph` interface implemented for each navigation context
- [ ] `Screen` objects defined for each screen
- [ ] `AppGraphsModule` created with all graphs registered
- [ ] `RouteRegistry` injected where needed
- [ ] Deep link patterns defined for parameterized routes
- [ ] `NavigationStore` used for large data
- [ ] ViewModels implement `InitializableViewModel` if needed
- [ ] Tests written for route resolution
- [ ] Documentation reviewed and understood

---

## Module Structure

```
core/navigation/
├── src/main/java/com/dxmxp/navigation/
│   ├── core/
│   │   ├── Navigator.kt           # Back stack wrapper
│   │   ├── NavigationStore.kt     # Data cache singleton
│   │   └── RouteRegistry.kt       # Route lookup + deep link resolution
│   ├── model/
│   │   ├── Route.kt               # Base interface
│   │   ├── Graph.kt               # Graph interface
│   │   ├── Screen.kt              # Screen helper + entry builders
│   │   └── RouteKey.kt            # Type-safe key wrapper
│   ├── di/
│   │   └── NavigationModule.kt    # Hilt configuration
│   ├── utils/
│   │   ├── DeepLinkHandler.kt     # URI → Route converter
│   │   └── NavigationUtils.kt     # Composable helpers
│   └── common/
│       └── InitializableViewModel.kt # ViewModel init contract
└── build.gradle.kts
```

---

## See Also

- **:seed** - App module with `AppGraphsModule` and example graphs
- **:feature:stories** - Feature module showing graph composition
- **:core:domain** - Domain layer with repositories
- **:core:ui** - UI components and theme

---

**Last Updated:** 2026-09-15  
**Maintained By:** Navigation Team  
**License:** Apache 2.0

