package com.example.data.remote.dto.rocket

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RocketResponse(
    val id: String,
    val name: String,
    val type: String,
    val height: Dimension,
    val diameter: Dimension,
    @SerialName("first_flight") val firstFlight: String,
    @SerialName("flickr_images") val images: List<String>,
    val description: String,
    val wikipedia: String,
) {
    @Serializable
    data class Dimension(
        val meters: Float,
    )
}
