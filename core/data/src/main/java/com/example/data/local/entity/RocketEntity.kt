package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.remote.dto.rocket.RocketResDto

@Entity(tableName = "rockets")
data class RocketEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val height: Float,
    val diameter: Float,
    val image: String,
    val firstFlight: String,
    val wikipedia: String,
    val description: String,
){
    fun toResDto() = RocketResDto(
        id = id,
        name = name,
        type = type,
        height = RocketResDto.Dimension(height),
        diameter = RocketResDto.Dimension(diameter),
        images = listOf(image),
        firstFlight = firstFlight,
        wikipedia = wikipedia,
        description = description
    )
}