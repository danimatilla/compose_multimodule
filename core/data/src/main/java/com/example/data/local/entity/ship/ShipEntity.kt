package com.example.data.local.entity.ship

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.remote.dto.ship.res.ShipResDto

@Entity(tableName = "ships")
data class ShipEntity(
    @PrimaryKey val id: String,
    val type: String,
    val yearBuilt: Int? = null,
    val homePort: String,
    val link: String? = null,
    val image: String? = null,
    val name: String,
    val active: Boolean,
){
    fun toShipDto() = ShipResDto(
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