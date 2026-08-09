package com.dxmxp.seed.navigation.routes

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.catalog.ProductDetailScreen
import com.dxmxp.seed.screens.catalog.ProductListScreen
import com.dxmxp.ui.navigation.model.Graph
import com.dxmxp.ui.navigation.model.Screen
import com.dxmxp.ui.navigation.model.Screen.Companion.screenEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
@Module
@InstallIn(SingletonComponent::class)
object CatalogGraph : Graph {

    override val screens: List<Class<out Screen>>
        get() = listOf(ProductList::class.java, ProductDetail::class.java)

    @Serializable
    data object ProductList : Screen

    @Serializable
    data class ProductDetail(val productId: Int) : Screen

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<ProductList> { ProductListScreen() }
        
        screenEntry<ProductDetail, com.dxmxp.seed.screens.catalog.ProductDetailViewModel>(
            viewModelProvide = { hiltViewModel() }
        ) { vm -> ProductDetailScreen(vm) }
    }

    override val route: String = "/catalog"

    @Provides @IntoSet
    override fun provideGraph(): Graph = CatalogGraph
}
