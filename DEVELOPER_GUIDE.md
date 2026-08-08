# 🧭 Ultimate Navigation Guide (v3.0)

This guide provides the technical details for working with the project's navigation system.

## 1. Core Principles

The architecture is built on three pillars:
1.  **Single Source of Truth**: The `Screen` object contains both the route and the data.
2.  **Decoupling**: ViewModels don't know about Compose; they use `NavigationManager`.
3.  **Orchestration**: `NavigationOrchestrator` decides which backstack handles an event, preventing "navigation leaks" between nested scaffolds.

---

## 2. Components Reference

| Component | Responsibility |
| :--- | :--- |
| `Screen` | A serializable destination. Defines visibility (e.g., `showMainBottomBar`). |
| `Graph` | A collection of `Screen`s. Must be registered in Hilt as a `Set<Graph>`. |
| `NavigationManager` | Singleton service to trigger events from anywhere (Business Logic). |
| `Navigator` | UI-layer interface (`LocalNavigator`). Bridges to `NavigationManager`. |
| `NavigationOrchestrator` | The "brain" that delegates events to the correct `NavBackStack`. |
| `RouteRegistry` | Central index of all routes for O(1) lookups and Deep Link resolution. |
| `ScaffoldController` | Manages local state (current tab, local backstack) for a Scaffold. |

---

## 3. Step-by-Step: Adding a New Feature

### Step 1: Define Screens
Create your screens in your module (e.g., `:stories` or a new library module). Always use `@Serializable`.

```kotlin
@Serializable
data object ProductList : Screen

@Serializable
data class ProductDetail(val productId: Int) : Screen
```

### Step 2: Create the Graph
Implement the `Graph` interface. Use `@Module` and `@Provides` to let the system discover it.

```kotlin
@Serializable
@Module
@InstallIn(SingletonComponent::class)
object ProductGraph : Graph {
    override val route = "/products"
    override val screens = listOf(ProductList::class.java, ProductDetail::class.java)

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<ProductList> { ProductListScreen() }
        screenEntry<ProductDetail, ProductDetailViewModel>(
            viewModelProvide = { hiltViewModel() }
        ) { vm -> ProductDetailScreen(vm) }
    }

    @Provides @IntoSet
    fun provideGraph(): Graph = ProductGraph
}
```

### Step 3: Handle Parameters in ViewModel
If your screen is a `data class` (parameterized), your ViewModel **must** implement `InitializableViewModel<S>` to receive the data. This is enforced at runtime by `screenEntry`.

```kotlin
@HiltViewModel
class ProductDetailViewModel @Inject constructor() : ViewModel(), InitializableViewModel<ProductDetail> {
    override fun init(screen: ProductDetail) {
        val id = screen.productId
        // Fetch product data...
    }
}
```

---

## 4. Advanced: Nested Scaffolds
If your feature has its own Bottom Bar or local backstack, use `rememberScaffoldController`.

```kotlin
@Composable
fun MyFeatureScaffold() {
    val orchestrator = hiltViewModel<ScaffoldViewModel>().orchestrator
    val controller = rememberScaffoldController(
        initialScreen = ProductList,
        graph = ProductGraph,
        orchestrator = orchestrator
    )

    Scaffold(
        bottomBar = { MyBottomBar(controller.currentDestination) }
    ) { padding ->
        MyNavGraph(backStack = controller.backStack, modifier = Modifier.padding(padding))
    }
}
```

---

## 5. Deep Linking
Deep links are resolved via `RouteRegistry`. A link like `myapp://products/productdetail?productId=42` will:
1. Be captured by `MainActivity`.
2. Passed to `DeepLinkHandler`.
3. `RouteRegistry` will find the matching `Screen` class, extract parameters from the URI query, and instantiate the screen using reflection (optimized).
4. A `PushScreen` event is triggered via `NavigationManager`.

> **Note**: For deep links to work, the `Screen` data class must have a primary constructor where parameter names match the URL query keys.

---

## 6. Modal Navigation & Delegation
The system supports "Modal" graphs (e.g., Full-screen overlays like Stories).
- Set `override val isModal = true` in your `Graph`.
- When a `PushScreen` event occurs, the `NavigationOrchestrator` checks if the target screen belongs to a modal graph.
- If the current local backstack is NOT modal, it will **delegate** the event to the parent backstack (Root).
- This ensures that modals always cover the entire UI regardless of where the event was triggered.

---

## 7. Custom Animations
You can use `NavigationUtils.modalAnimation()` inside your `registerEntries` to apply standardized transitions.

---

## 8. Back Press Handling
Use `LocalBackPressHandler` to intercept back events at the screen level (e.g., to show a confirmation dialog).

```kotlin
val backPressHandler = rememberBackPressHandler {
    if (hasUnsavedChanges) {
        showExitDialog = true
        false // Prevent back
    } else {
        true // Allow back
    }
}

CompositionLocalProvider(LocalBackPressHandler provides backPressHandler) {
    Content(...)
}
```
