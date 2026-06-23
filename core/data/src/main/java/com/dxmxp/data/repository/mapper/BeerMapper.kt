package com.dxmxp.data.repository.mapper

import com.dxmxp.data.remote.dto.beer.BeerResponse
import com.dxmxp.domain.model.Beer

object BeerMapper {

    /**
     * Maps [BeerResponse] (Remote DTO) to [Beer] (Domain Model).
     */
    fun BeerResponse.toDomain(): Beer =
        Beer(
            id = "$id",
            name = name,
            volumeValue = volume?.value?.toDouble(),
            volumeUnit = volume?.unit,
            image = image,
        )
}
