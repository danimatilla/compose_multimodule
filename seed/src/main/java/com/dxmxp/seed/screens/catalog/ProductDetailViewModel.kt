package com.dxmxp.seed.screens.catalog

import com.dxmxp.domain.model.Product
import com.dxmxp.seed.navigation.routes.CatalogGraph
import com.dxmxp.ui.base.BaseViewModel
import com.dxmxp.ui.common.InitializableViewModel
import com.dxmxp.ui.navigation.core.NavigationManager
import com.dxmxp.ui.navigation.core.NavigationStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val navigationStore: NavigationStore
) : BaseViewModel<ProductDetailViewModel.State, Unit, ProductDetailViewModel.Event>(),
    InitializableViewModel<CatalogGraph.ProductDetail> {

    data class State(
        val product: Product? = null,
        val isLoading: Boolean = false
    )

    sealed interface Event {
        data class OnRateProduct(val newRating: Double) : Event
    }

    override fun createInitialState(): State = State()

    override fun init(screen: CatalogGraph.ProductDetail) {
        val key = "product_${screen.productId}"
        
        // 1. Intentamos recuperar el objeto del store
        val cachedProduct: Product? = navigationStore.getData(key)
        
        if (cachedProduct != null) {
            setState { copy(product = cachedProduct) }
        } else {
            // Si no está (ej: Deep Link), cargaríamos de red aquí
        }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnRateProduct -> rateProduct(event.newRating)
        }
    }

    private fun rateProduct(rating: Double) {
        val currentProduct = uiState.value.product ?: return
        val updatedProduct = currentProduct.copy(rating = rating)
        
        setState { copy(product = updatedProduct) }

        // 2. Emitimos el resultado de vuelta al origen
        // Esto es opcional, solo lo hacemos porque hubo un cambio
        navigationStore.emitResult("product_${currentProduct.id}", updatedProduct)
    }
}
