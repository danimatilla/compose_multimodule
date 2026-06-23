package com.dxmxp.ui.model

import com.dxmxp.domain.model.Beer

data class BeerUiModel(
    val id: String,
    val name: String,
    val volumeText: String,
    val imageUrl: String
)

fun Beer.toUiModel(): BeerUiModel =
    BeerUiModel(
        id = id,
        name = name,
        volumeText = volumeValue?.let { "$volumeValue $volumeUnit" } ?: "N/A",
        imageUrl = image
    )
