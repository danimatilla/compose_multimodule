package com.dxmxp.domain.model

data class RocketBo(
    val id: String,
    val name: String,
    val height: Float,
    val firstFlight: String,
    val images: List<String>,
)

data class RocketDetailBo(
    val id: String,
    val name: String,
    val type: String,
    val height: Float,
    val diameter: Float,
    val firstFlight: String,
    val images: List<String>,
    val description: String,
    val wikipedia: String,
)
