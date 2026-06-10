package com.dxmxp.data.repository.mapper

import com.dxmxp.data.remote.dto.rocket.RocketResponse
import com.dxmxp.domain.model.Rocket

object RocketMapper {

    fun RocketResponse.toDomain(): Rocket =
        Rocket(
            id = id,
            name = name,
            height = height.toMeters(),
            firstFlight = firstFlight,
            images = flickrImages
        )

    private fun RocketResponse.Dimension.toMeters(): Float =
        meters ?: (feet?.let { it * 0.3048f } ?: 0f)
}
