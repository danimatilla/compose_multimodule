package com.dxmxp.seed.screens.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val product = state.product

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (product != null) {
                Text(text = product.title, style = MaterialTheme.typography.headlineLarge)
                Text(text = "Brand: ${product.brand}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.description)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Price: ${product.price}€", style = MaterialTheme.typography.headlineSmall)
                Text(text = "Current Rating: ${product.rating} ★")
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { 
                    viewModel.setEvent(ProductDetailViewModel.Event.OnRateProduct(5.0)) 
                }) {
                    Text("Rate 5 Stars")
                }
            } else {
                CircularProgressIndicator()
            }
        }
    }
}
