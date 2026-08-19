# 🧭 Ultimate Navigation Guide (v3.1)

Technical deep-dive into the hierarchical navigation system.

## 1. Core Principles

1.  **Hierarchy of Responsibility**: The Root (Activity) handles high-level transitions (Graphs). Scaffolds handle internal screens.
2.  **Access Guard**: Auth state is checked at the point of navigation, not inside screens.
3.  **Event Persistence**: `NavigationManager` uses `replay = 1`. If a deep link is sent before a Scaffold is ready, the Scaffold will "catch up" as soon as it subscribes.

---

## 2. Component Logic

### NavigationOrchestrator
The orchestrator determines if an event `appliesHere` based on the backstack context:
- **At Root**: Handles events for `Graph`s or routes that don't belong to the currently active Scaffold.
- **In Scaffold**: Handles events for routes strictly inside its own `Graph`. It ignores events referring to itself to prevent infinite recursion.

### RouteRegistry
A centralized index generated at startup. It uses Kotlin Reflection to:
1.  Map route strings (URLs) to singleton objects or data classes.
2.  Parse query parameters into constructor arguments.
3.  Support optional/nullable parameters by verifying constructor metadata.

---

## 3. Handling Auth
The `requiresAuth` property (default: `true`) is checked by the `NavigationOrchestrator`.

```kotlin
// In AuthGraph.kt
object AuthGraph : Graph {
    override val requiresAuth = false // Publicly accessible
}
```

If a protected route is requested without a session, the Orchestrator performs a `setRoot(AuthGraph)` automatically.

---

## 4. Deep Link Execution Flow

1.  **Intent** received in `MainActivity`.
2.  `DeepLinkHandler` delegates to `RouteRegistry`.
3.  `RouteRegistry` identifies the `Screen` and instantiates it with URL parameters.
4.  A `PushScreen` event is emitted.
5.  **Root Orchestrator** checks if the screen belongs to a different Scaffold.
6.  If so, it **auto-opens** that Scaffold first.
7.  The new **Scaffold Orchestrator** receives the same event (via `replay=1`) and navigates internally to the final destination.

---

## 5. Modal vs Regular Graphs

- **Regular Graph**: Typically used for main sections. Navigating to one uses `setRoot`.
- **Modal Graph** (`isModal = true`): Used for overlays (e.g., Stories). Root will `push` these instead of `setRoot` to allow going back.

---

## 6. Passing Large Data & Results

Use `NavigationStore` for non-primitive data or large objects.

### Step 1: Source ViewModel
```kotlin
navigationStore.pushData("story_key", heavyStoryModel)
navManager.push(StoryDetail(id = "1"))
```

### Step 2: Destination ViewModel
```kotlin
override fun init(screen: StoryDetail) {
    val story = navigationStore.getData<Story>("story_key")
}
```

---

## 7. Custom Transitions
Use `metadata` in `screenEntry` to define animations.

```kotlin
screenEntry<MyScreen>(
    metadata = metadata { modalAnimation() }
) { ... }
```

Standardized animations are available in `NavigationUtils`.
