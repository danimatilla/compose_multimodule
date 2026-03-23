package com.example.data.remote.dto.rocket

import com.example.data.local.entity.RocketEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RocketResDto(
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

    fun toEntity() = RocketEntity(
        id = id,
        name = name,
        type = type,
        height = height.meters,
        diameter = diameter.meters,
        firstFlight = firstFlight,
        image = images.firstOrNull().orEmpty(),
        description = description,
        wikipedia = wikipedia,
    )
}
