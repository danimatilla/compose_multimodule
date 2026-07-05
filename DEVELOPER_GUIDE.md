# 🧭 Guía Definitiva de Navegación para Desarrolladores (v2.0)

Esta guía centraliza toda la información necesaria para trabajar con el sistema de navegación del proyecto.

---

## 1. Visión General y Arquitectura

El sistema de navegación está basado en **Navigation3** y ha sido optimizado para eliminar la duplicación de código y el uso excesivo de reflexión.

### Flujo de Navegación
```
┌─────────────────────────┐      ┌─────────────────────────┐      ┌─────────────────────────┐
│  Acción en UI           │      │  NavigationManager      │      │  NavigationOrchestrator │
│  (Navigator.push, etc)  ├─────►│  (Emite Evento)         ├─────►│  (Recibe y Delega)      │
└─────────────────────────┘      └─────────────────────────┘      └────────────┬────────────┘
                                                                               │
                                                                               ▼
                                                                  ┌────────────┴────────────┐
                                                                  │ ¿Dónde se aplica?       │
                                                                  ├─────────────────────────┤
                                                                  │ ► Root (MainActivity)   │
                                                                  │ ► Graph (Scaffolds)     │
                                                                  └─────────────────────────┘
```

---

## 2. Definición de Pantallas (`Screen`)

Cada destino es un `object` o `data class` marcado con `@Serializable`.

```kotlin
// Pantalla sin parámetros
@Serializable
data object HomeScreen : Screen

// Pantalla con parámetros (Data Class)
@Serializable
data class DetailScreen(
    val id: String, 
    val title: String,
    val story: Story? = null // Paso de datos directo (Serializable)
) : Screen {
    override val showMainBottomBar: Boolean = false // Control de visibilidad de UI
}
```

---

## 3. Registro de Grafos (`Graph`)

Los grafos agrupan pantallas relacionadas. Registra las pantallas explícitamente en `screens` para optimizar el rendimiento.

```kotlin
@Serializable
@Module
@InstallIn(SingletonComponent::class)
object FeatureGraph : Graph {
    override val route: String = "/feature"
    override val isModal: Boolean = false // Si es true, se maneja como flujo independiente (ej. Stories)

    // RECOMENDADO: Listado explícito para evitar reflexión lenta
    override val screens = listOf(
        Home::class.java,
        Detail::class.java
    )

    override fun EntryProviderScope<NavKey>.registerEntries() {
        // Registro simple
        screenEntry<Home> { HomeScreen() }
        
        // Registro con ViewModel e inicialización automática
        screenEntry<Detail, DetailViewModel>(
            viewModelProvide = { hiltViewModel() }
        ) { vm -> DetailScreen(vm) }
    }

    @Provides @IntoSet
    override fun provideGraph(): Graph = FeatureGraph
}
```

---

## 4. Cómo Navegar

### Desde un Composable
Usa `LocalNavigator.current`.

```kotlin
val navigator = LocalNavigator.current
Button(onClick = { 
    navigator.push(DetailScreen(id = "1", title = "Ejemplo")) 
}) {
    Text("Ir al Detalle")
}
```

### Desde un ViewModel
Inyecta `NavigationManager` para disparar eventos desde la lógica de negocio.

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {
    fun onActionComplete() {
        navigationManager.push(HomeScreen)
    }
}
```

---

## 5. Gestión de Datos y ViewModels

### Implementar `InitializableViewModel`
Si tu pantalla tiene parámetros, implementa esta interfaz para recibirlos automáticamente al entrar a la pantalla.

```kotlin
@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel(), InitializableViewModel<DetailScreen> {
    
    override fun init(screen: DetailScreen) {
        // Los datos llegan directamente aquí sin necesidad de DataObserver
        val id = screen.id
        val story = screen.story
    }
}
```

---

## 6. Implementación de Scaffolds

Para flujos complejos con su propio backstack y barra inferior, usa `rememberScaffoldController`.

```kotlin
@Composable
fun FeatureScaffold() {
    val orchestrator = hiltViewModel<ScaffoldViewModel>().orchestrator
    
    val controller = rememberScaffoldController(
        initialScreen = FeatureGraph.Home,
        graph = FeatureGraph,
        orchestrator = orchestrator
    )

    SeedScaffold(
        bottomBar = {
            BottomBar(
                currentDestination = controller.currentDestination,
                // Los ítems se sincronizan automáticamente con el controller
            )
        }
    ) { padding ->
        FeatureNavGraph(
            backStack = controller.backStack,
            modifier = Modifier.padding(padding)
        )
    }
}
```

---

## 7. Preguntas Frecuentes (FAQ)

**P: ¿Cómo manejo el botón "Atrás" del sistema?**
R: Usa `BackPressHandler` para validaciones personalizadas (ej. "¿Desea guardar cambios?").

**P: ¿Cómo funcionan los Deep Links?**
R: `RouteRegistry` mapea URIs a `Screen` automáticamente. Solo asegúrate de que la pantalla esté registrada en un `Graph`.

**P: ¿Cómo paso datos pesados?**
R: Siempre que el objeto sea `@Serializable`, puedes pasarlo directamente en el constructor de la `Screen`.

**P: ¿Dónde se definen las animaciones?**
R: En `registerEntries()` usando `metadata { modalAnimation() }`.

---

## 8. Resumen de Componentes

| Componente | Responsabilidad |
| :--- | :--- |
| `NavigationManager` | Singleton para disparar eventos (Push, Pop, SetRoot). |
| `NavigationOrchestrator` | El "cerebro" que decide qué backstack debe procesar el evento. |
| `ScaffoldController` | Gestiona el estado reactivo local de un Scaffold. |
| `RouteRegistry` | Índice central de todas las rutas registradas (O(1) lookups). |
| `Screen` | Representación serializable de un destino. |
