package com.example.data.remote.dto.ship.res

import com.example.data.local.entity.ship.ShipEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShipResDto(
    val id: String,
    val type: String,
    @SerialName("year_built") val yearBuilt: Int? = null,
    @SerialName("home_port") val homePort: String,
    val link: String? = null,
    val image: String? = null,
    val name: String,
    val active: Boolean,
) {
    fun toShipEntity() = ShipEntity(
        id = id,
        type = type,
        yearBuilt = yearBuilt,
        homePort = homePort,
        link = link,
        image = image,
        name = name,
        active = active
    )
}
