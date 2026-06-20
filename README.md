# Seed Project - Android Compose Multi-módulo

Este es un **Proyecto Seed de Android** de alto rendimiento diseñado para aplicaciones de gran escala. Implementa una arquitectura modular moderna utilizando **Jetpack Compose**, **Navigation 3** y los principios de **Clean Architecture**.

El corazón de este proyecto es su **Infraestructura Genérica**, un conjunto de utilidades, clases base y extensiones diseñadas para eliminar el código repetitivo y garantizar la robustez en todas las capas.

---

## 🚀 Arquitectura del Sistema

El proyecto está dividido en módulos desacoplados para mejorar la escalabilidad y los tiempos de compilación:

- **`:seed`**: App Shell. Punto de entrada, configuración de Hilt y orquestación de la navegación raíz.
- **`:core:domain`**: Lógica de negocio pura (Kotlin). Contiene modelos inmutables, `BaseUseCase` y gestión de estados de datos.
- **`:core:data`**: Implementación de datos. Gestiona red (Retrofit), persistencia (Room), interceptores de errores y lógica de paginación.
- **`:core:ui`**: Sistema de diseño y utilidades de UI. Contiene componentes comunes, extensiones de Compose y helpers de navegación.
- **`:stories`**: Módulo de característica (Feature module) que sirve como referencia de implementación.

---

## 🧠 Arquitectura de UI: Patrón MVI

La capa de presentación implementa el patrón **MVI (Model-View-Intent)** mediante la clase `BaseViewModel`, asegurando un **Flujo Unidireccional de Datos (UDF)**:

1.  **State (Estado)**: Representa el estado único de la UI en un momento dado. Se gestiona mediante `StateFlow`.
2.  **Event (Evento/Intento)**: Acciones del usuario o eventos del sistema que disparan un cambio de estado.
3.  **Effect (Efecto)**: Acciones de un solo disparo que no modifican el estado de forma persistente (ej. navegación, mostrar un Snackbar). Se gestiona mediante un `Channel`.

---

## ✨ Infraestructura Genérica (Core Helpers)

### 1. Dominio y Casos de Uso (`:core:domain`)

#### `BaseUseCase` y `BaseFlowUseCase`
Estandarizan la ejecución de lógica de negocio asíncrona.
```kotlin
// Ejemplo de FlowUseCase
class GetItemsUseCase @Inject constructor(private val repo: Repo) : BaseFlowUseCase<Unit, DataResult<List<Item>>>() {
    override fun launch(params: Unit) = repo.getItems()
}
```

#### `DataResult<T>` y `AppException`
Máquina de estados para la transferencia de datos y jerarquía de errores semánticos.
```kotlin
val result: DataResult<String> = DataResult.Success("Hola")
val error = AppException.NoInternetException(IOException())
```

### 2. Datos y Comunicación Remota (`:core:data`)

#### `NetworkHandler` y `safeCall`
Centraliza llamadas seguras gestionando automáticamente excepciones.
```kotlin
suspend fun getData() = networkHandler.safeCall { api.getData() }
```

#### `Paginator` y `PaginationHandler`
Abstracción genérica para control de claves y acumulación de listas. El `Paginator` permite una integración limpia con `NetworkHandler` evitando anidación excesiva mediante referencias a funciones:
```kotlin
// Implementación optimizada en DataSource
override suspend fun fetchData(shouldReset: Boolean) = withContext(dispatcher) {
    if (shouldReset) paginator.reset()
    networkHandler.safeCall { 
        paginator.fetchPaged(api::fetchFromRemote) 
    }
}
```

#### `pagingFlow` y `loadingFlow`
Extensiones de `DataResult` para convertir llamadas suspendidas en flujos de estados.

### 3. UI, Compose y Ciclo de Vida (`:core:ui`)

#### `BaseViewModel<STATE, EFFECT, EVENT>`
Base para la implementación de MVI.
```kotlin
abstract class BaseViewModel<STATE, EFFECT, EVENT> : ViewModel() {
    val uiState: StateFlow<STATE>
    val effect: Flow<EFFECT>
    fun setEvent(event: EVENT)
    protected fun setState(reduce: STATE.() -> STATE)
    protected fun setEffect(builder: () -> EFFECT)
}
```

#### `launchResultFlow` y `launchFlow`
Simplifican la recolección de flujos en el `viewModelScope` actualizando el estado de la UI.
```kotlin
viewModelScope.launchResultFlow(
    flow = useCase(params),
    setState = { update -> setState { update() } },
    onSuccess = { data, endReached -> copy(items = data, endReached = endReached) }
)
```

#### `InfiniteScrollHandler` y `SeedPullRefresh`
Componentes visuales para gestión de listas infinitas y refresco.
```kotlin
InfiniteScrollHandler(listState, isLoading, endReached) { viewModel.setEvent(Event.LoadNextPage) }
SeedPullRefresh(isLoading, onRefresh = { viewModel.setEvent(Event.Refresh) }) { /* Content */ }
```

#### `DataObserver` y `debounce`
Comunicación entre pantallas y optimización de eventos (ej. búsquedas).

### 4. Navegación Avanzada (`:core:ui:navigation`)

#### `NavigationHandler` y `screenEntry`
Sistema de navegación desacoplado basado en eventos y registro automático de pantallas.

#### `DeepLinkHandler` y `modalAnimation`
Resolución dinámica de URIs y transiciones visuales.

---

## 🛠️ Ejemplos de Implementación Técnica Completa

### 1. Capa de Datos: Remote DataSource (Implementación Optimizada)
Se utiliza el `NetworkHandler` para llamadas seguras y el `Paginator` para la lógica de claves. La anidación se reduce al mínimo mediante el uso de referencias a funciones (`api::fetchItems`).

```kotlin
class MyRemoteDataSourceImpl @Inject constructor(
    private val api: MyApi,
    private val networkHandler: NetworkHandler,
    private val paginator: Paginator<Int, List<MyResponse>>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : MyRemoteDataSource {

    override suspend fun fetchItems(shouldReset: Boolean): List<MyResponse>? = withContext(dispatcher) {
        if (shouldReset) paginator.reset()
        networkHandler.safeCall {
            paginator.fetchPaged(api::fetchFromRemote)
        }
    }
}
```

### 2. Capa de Datos: Repositorio con `pagingFlow`
El repositorio orquestra el DataSource y el mapeo a dominio, utilizando `pagingFlow` para gestionar automáticamente la emisión de estados (`Loading`, `Success`, `Error`) y la acumulación de la lista (caché) a través del `PaginationHandler`.

```kotlin
@Singleton
class MyRepositoryImpl @Inject constructor(
    private val remoteDataSource: MyRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    paginationHandlerFactory: PaginationHandler.Factory<MyDomainModel>
) : MyRepository {
    
    private val paginationHandler = paginationHandlerFactory.create(pageSize = 20)

    override fun getItems(shouldReset: Boolean): Flow<DataResult<List<MyDomainModel>?>> =
        DataResult.pagingFlow(
            dispatcher = ioDispatcher,
            paginationHandler = paginationHandler,
            shouldReset = shouldReset
        ) {
            remoteDataSource.fetchItems(shouldReset)?.map { it.toDomain() }
        }
}
```

### 3. Implementación de un ViewModel (MVI) con Inicialización
```kotlin
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetailUseCase: GetDetailUseCase
) : BaseViewModel<State, Effect, Event>(), InitializableViewModel<MyScreen.Detail> {

    override fun createInitialState() = State()

    override fun init(screen: MyScreen.Detail) {
        setEvent(Event.LoadDetail(screen.id))
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.LoadDetail -> loadDetail(event.id)
        }
    }

    private fun loadDetail(id: String) {
        viewModelScope.launchResultFlow(
            flow = getDetailUseCase(id),
            setState = { update -> setState { update() } },
            onLoading = { setState { copy(isLoading = it) } },
            onSuccess = { data, _ -> copy(detail = data) }
        )
    }
}
```

---

## 📦 Stack Tecnológico
- **Arquitectura de UI**: MVI (Model-View-Intent).
- **Core**: Kotlin Coroutines & Flow.
- **UI**: Jetpack Compose, Material 3.
- **DI**: Hilt (Dagger) + Assisted Injection.
- **Red**: Retrofit, OkHttp, Kotlinx Serialization.
- **Persistencia**: Room Database.
- **Navegación**: Navigation 3.0.
