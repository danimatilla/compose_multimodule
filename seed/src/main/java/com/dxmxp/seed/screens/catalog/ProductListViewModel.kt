package com.dxmxp.seed.screens.catalog

import androidx.lifecycle.viewModelScope
import com.dxmxp.domain.model.Product
import com.dxmxp.seed.navigation.routes.CatalogGraph
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.navigation.core.NavigationManager
import com.dxmxp.ui.navigation.core.NavigationStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val navigationStore: NavigationStore
) : BaseViewModel<ProductListViewModel.State, Unit, ProductListViewModel.Event>() {

    data class State(
        val products: List<Product> = emptyList(),
        val isLoading: Boolean = false
    )

    sealed interface Event {
        data class OnProductClick(val product: Product) : Event
    }

    override fun createInitialState(): State = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnProductClick -> navigateToDetail(event.product)
        }
    }

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        val dummyProducts = listOf(
            Product(
                id = 1,
                title = "iPhone 9",
                description = "An apple mobile which is nothing like apple",
                price = 549.0,
                rating = 4.69,
                stock = 94,
                brand = "Apple",
                category = "smartphones",
                thumbnail = "...",
                images = emptyList()
            ),
            Product(
                id = 2,
                title = "Samsung Universe 9",
                description = "Samsung's new variant which goes beyond Galaxy",
                price = 1249.0,
                rating = 4.09,
                stock = 36,
                brand = "Samsung",
                category = "smartphones",
                thumbnail = "...",
                images = emptyList()
            )
        )
        setState { copy(products = dummyProducts) }
    }

    private fun navigateToDetail(product: Product) {
        val key = "product_${product.id}"
        
        // 1. Guardamos el objeto pesado en el store
        navigationStore.pushData(key, product)

        // 2. Nos suscribimos al retorno (Opcional)
        // Si el detalle emite un cambio, actualizamos nuestra lista
        viewModelScope.launch {
            navigationStore.observeResult<Product>(key)
                .take(1)
                .collect { updatedProduct ->
                    updateProductInList(updatedProduct)
                }
        }

        // 3. Navegamos pasando solo el ID
        navigationManager.push(CatalogGraph.ProductDetail(productId = product.id))
    }

    private fun updateProductInList(updatedProduct: Product) {
        setState {
            copy(products = products.map { if (it.id == updatedProduct.id) updatedProduct else it })
        }
    }
}
