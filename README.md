# Compose Multi-Module Navigation Architecture

A robust, type-safe, and scalable navigation architecture for Jetpack Compose based on **Navigation3**. This project demonstrates a multi-module setup designed for large-scale Android applications.

## 🚀 Key Features

- **Type-Safe Navigation**: Destinations are defined as `@Serializable` objects or data classes.
- **Multi-Module Support**: Decoupled navigation logic allowing features to be developed in isolation.
- **Orchestrated Backstacks**: Intelligent handling of multiple backstacks (e.g., Root, Main Tab Bar, Nested Scaffolds).
- **Zero-Boilerplate Data Passing**: Pass data directly through Screen constructors with automatic ViewModel initialization.
- **Reflection-Optimized Routing**: Fast route resolution and deep link handling via a centralized `RouteRegistry` that maps URLs to `@Serializable` screen instances.
- **Common Screens Library**: Built-in support for reusable screens like `WebView` across all modules.
- **Strict Architecture**: Enforced patterns via custom Konsist tests to ensure consistent navigation implementation.

---

## 🛠 Architecture Overview

### 1. Navigation Flow
The navigation follows a decoupled, event-driven pattern. This ensures that features remain independent and navigation can be triggered from anywhere (UI or ViewModels).

```mermaid
graph LR
    UI[UI Action] --> NM[NavigationManager]
    NM --> NO[NavigationOrchestrator]
    NO --> BS[Target Backstack]
```

1.  **UI Action**: A user interaction (e.g., button click) or a business logic decision.
2.  **NavigationManager**: Acts as a central "Post Office". It captures navigation requests and broadcasts them as `NavigationEvent` objects. This allows ViewModels to trigger navigation without having any reference to the UI.
3.  **NavigationOrchestrator**: The "Brain" of the system. It listens to the stream of events and determines which part of the app (the Root backstack, a nested scaffold, or a specific feature flow) should handle the request.
4.  **Target Backstack**: The specific `NavBackStack` (from Navigation3) that gets updated. Changing the state of the backstack is what finally triggers the UI recomposition to show the new screen.

### 2. Package Structure (`core:ui`)
- `navigation.model`: Core entities (`Screen`, `Graph`).
- `navigation.core`: Infrastructure (`Navigator`, `NavigationManager`, `RouteRegistry`, `NavigationEvent`).
- `navigation.orchestration`: Coordination logic (`NavigationOrchestrator`).
- `navigation.scaffold`: UI controllers and helpers (`ScaffoldController`, `BackPressHandler`).
- `navigation.utils`: Utilities (`DeepLinkHandler`, `NavigationUtils`).

---

## 📖 Implementation Guide

### 1. Define a Screen
Screens are `NavKey` implementations. Use `data object` for simple destinations and `data class` for parameterized ones.

```kotlin
@Serializable
data object HomeScreen : Screen

@Serializable
data class DetailScreen(val id: String, val title: String) : Screen {
    override val showMainBottomBar = false
}
```

### 2. Create a Navigation Graph
Graphs group related screens and manage their registration.

```kotlin
@Serializable
@Module
@InstallIn(SingletonComponent::class)
object MainGraph : Graph {
    override val route = "/main"
    
    // Explicitly list screens for performance and safety
    override val screens = listOf(Home::class.java, Detail::class.java)

    override fun EntryProviderScope<NavKey>.registerEntries() {
        // Simple screen without VM
        screenEntry<Home> { HomeScreen() }
        
        // Screen with automatic ViewModel initialization
        screenEntry<Detail, DetailViewModel>(
            viewModelProvide = { hiltViewModel() }
        ) { vm -> DetailScreen(vm) }
    }

    @Provides @IntoSet
    fun provideGraph(): Graph = MainGraph
}
```

### 3. Automatic ViewModel Initialization
If your ViewModel implements `InitializableViewModel<S>`, it will receive the screen parameters automatically when the destination is reached.

```kotlin
@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel(), InitializableViewModel<DetailScreen> {
    override fun init(screen: DetailScreen) {
        val id = screen.id // Data is ready to use
    }
}
```

### 4. Navigating

#### From Composables
Use `LocalNavigator.current`:
```kotlin
val navigator = LocalNavigator.current
Button(onClick = { navigator.push(DetailScreen(id = "1", title = "Hello")) }) {
    Text("Go to Detail")
}
```

#### From ViewModels
Inject `NavigationManager`:
```kotlin
class MyViewModel @Inject constructor(private val navManager: NavigationManager) : ViewModel() {
    fun onComplete() = navManager.setRoot(HomeScreen)
}
```

### 5. Using Common Screens
The `core:ui` module provides common screens that can be used anywhere.

```kotlin
// From any ViewModel
navManager.push(WebView(url = "https://example.com", title = "Help"))
```

---

## 🔗 Deep Links
The system handles deep links automatically using the `RouteRegistry`. Simply register your graph, and URLs matching `your-app://your-domain/screen-name?param=value` will be mapped to the corresponding `Screen` instance.

## 🏗 Modularization Strategy
- `:core:ui`: Common navigation components, Design System, and common screens.
- `:core:domain` / `:core:data`: Business logic and persistence.
- `:stories`: Feature-specific module. Exports its `Graph` via Hilt.
- `:seed`: The "App" module that collects all `Graph`s, sets up the Root backstack, and contains local features (like Auth).

```mermaid
graph TD
    APP[":seed (App)"] --> F1[":stories"]
    APP --> CUI[":core:ui"]
    F1 --> CUI
    CUI --> CD[":core:domain"]
    CD --> CDAT[":core:data"]
```
