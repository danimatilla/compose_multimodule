package com.dxmxp.data.repository.mapper

import com.dxmxp.data.local.entity.RocketEntity
import com.dxmxp.data.remote.dto.rocket.RocketResponse
import com.dxmxp.domain.model.Rocket

/**
 * Mapper for Rocket related data transfers between layers.
 */
object RocketMapper {

    /**
     * Maps [RocketResponse] (Remote DTO) to [Rocket] (Domain Model).
     */
    fun RocketResponse.toDomain(): Rocket =
        Rocket(
            id = id,
            name = name,
            height = height.toMeters(),
            firstFlight = firstFlight,
            images = flickrImages,
        )

    /**
     * Maps [RocketEntity] (Local Database Entity) to [Rocket] (Domain Model).
     */
    fun RocketEntity.toDomain(): Rocket =
        Rocket(
            id = id,
            name = name,
            height = height,
            firstFlight = firstFlight,
            images = listOf(image),
        )

    /**
     * Maps [RocketResponse] (Remote DTO) to [RocketEntity] (Local Database Entity).
     */
    fun RocketResponse.toEntity(): RocketEntity =
        RocketEntity(
            id = id,
            name = name,
            type = type,
            height = height.toMeters(),
            diameter = diameter.toMeters(),
            image = flickrImages.firstOrNull() ?: "",
            firstFlight = firstFlight,
            wikipedia = wikipedia,
            description = description,
        )

    private fun RocketResponse.Dimension.toMeters(): Float =
        meters ?: (feet?.let { it * 0.3048f } ?: 0f)
}
