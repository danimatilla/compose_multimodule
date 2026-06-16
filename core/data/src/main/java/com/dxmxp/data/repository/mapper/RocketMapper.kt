package com.dxmxp.data.repository.mapper

import com.dxmxp.data.local.entity.BeerEntity
import com.dxmxp.data.remote.dto.rocket.BeerResponse
import com.dxmxp.domain.model.Beer

/**
 * Mapper for Rocket related data transfers between layers.
 */
object RocketMapper {

    /**
     * Maps [BeerResponse] (Remote DTO) to [Beer] (Domain Model).
     */
    fun BeerResponse.toDomain(): Beer =
        Beer(
            id = "$id",
        )

    /**
     * Maps [BeerEntity] (Local Database Entity) to [Beer] (Domain Model).
     */
    fun BeerEntity.toDomain(): Beer =
        Beer(
            id = id,
        )

    /**
     * Maps [BeerResponse] (Remote DTO) to [BeerEntity] (Local Database Entity).
     */
    fun BeerResponse.toEntity(): BeerEntity =
        BeerEntity(
            id = "$id",
        )
}
