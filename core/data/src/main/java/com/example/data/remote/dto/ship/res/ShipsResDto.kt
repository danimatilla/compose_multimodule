package com.example.data.remote.dto.ship.res

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShipsResDto(
    @SerialName("docs") val ships: List<ShipResDto>,
    val page: Int,
    val hasNextPage: Boolean,
    val nextPage: Int? = null,
)
