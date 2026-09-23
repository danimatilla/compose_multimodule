# 🧭 Navigation Developer Guide (Simplified Nav 3)

Technical deep-dive into the simplified hierarchical navigation system using Navigation 3.

## 1. Core Principles

1.  **Hierarchy of Responsibility**: The Root (Activity) handles high-level transitions (Graphs). Scaffolds handle internal screens.
2.  **Explicit Registration**: Routes and deep links are registered explicitly, avoiding reflection for better performance and clarity.
3.  **Scoped Navigators**: Use `LocalNavigator` for navigation within the current scope.

---

## 2. Component Logic

### Navigator
A simple wrapper around `NavBackStack`. It provides basic operations like `push`, `pop`, `setRoot`, and atomic stack updates via `updateStack { ... }`.
- Provided via `LocalNavigator`.
- Decoupled from ViewModels; navigation is triggered via UI observing ViewModel effects.

### RouteRegistry
A centralized registry for Deep Linking. It maps URL patterns to route creator functions.
- Registration is manual and explicit.
- No reflection used for instantiation.

---

## 3. Handling Auth
Authentication state is checked in `MainActivity` during initialization and when processing deep links. 
- ViewModels can emit a `NavigateToAuth` effect if a session expires.
- The UI layer (Screens) catches this effect and calls `navigator.setRoot(AuthGraph)`.

---

## 4. Deep Link Execution Flow

1.  **Intent** received in `MainActivity`.
2.  `DeepLinkHandler` delegates to `RouteRegistry`.
3.  `RouteRegistry` finds the creator for the path and returns a `Route` instance.
4.  `MainActivity` calls `navigator.updateStack { ... }` or `navigator.push(route)`.
5.  If the route belongs to a nested graph, the Root navigator pushes that graph, and the graph's initial route handles the rest.

---

## 5. Multiple Backstacks

Each scaffold (e.g., `MainScaffold`) creates its own `NavBackStack` using `rememberNavBackStack`.
- This backstack is independent of the root one.
- Navigation inside the scaffold only affects the nested stack.
- State is preserved when switching between top-level graphs if managed correctly at the root.

---

## 6. Passing Large Data & Results

Use `NavigationStore` for non-primitive data or large objects to keep routes clean and avoid `TransactionTooLargeException`.

### Step 1: Source Screen/ViewModel
```kotlin
// Emitting effect with data
setEffect { Effect.OpenDetail(heavyStoryModel) }

// In Screen
navigator.navigate(StoryDetail(id = story.id, story = story))
```
*Note: Simple data classes can be passed directly if they are @Serializable. For truly large objects, use NavigationStore.*

---

## 7. Custom Transitions
Use `metadata` in `screenEntry` to define animations.

```kotlin
screenEntry<MyScreen>(
    metadata = metadata { modalAnimation() }
) { ... }
```

Standardized animations are available in `NavigationUtils`.
