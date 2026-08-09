package com.dxmxp.seed.screens.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { Text("Product Catalog", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(state.products) { product ->
                ListItem(
                    headlineContent = { Text(product.title) },
                    supportingContent = { Text("Rating: ${product.rating} ★") },
                    trailingContent = { Text("${product.price}€") },
                    modifier = Modifier.clickable { 
                        viewModel.setEvent(ProductListViewModel.Event.OnProductClick(product))
                    }
                )
            }
        }
    }
}
