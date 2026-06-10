package com.dxmxp.domain.model

data class Rocket(
    val id: String,
    val name: String,
    val height: Float,
    val firstFlight: String,
    val images: List<String>,
)
