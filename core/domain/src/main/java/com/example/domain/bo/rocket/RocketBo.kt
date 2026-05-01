package com.example.domain.bo.rocket

data class RocketBo(
    val id: String,
    val name: String,
    val type: String,
    val height: Float,
    val diameter: Float,
    val firstFlight: String,
    val images: List<String>,
    val description: String
)
