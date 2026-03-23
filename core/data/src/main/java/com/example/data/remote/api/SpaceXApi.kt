package com.example.data.remote.api

import com.example.data.remote.dto.rocket.RocketResDto
import com.example.data.remote.dto.ship.ShipResDto
import com.example.data.remote.dto.ship.ShipsResDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

sealed interface SpaceXApi {
    interface Ships : SpaceXApi{

        @POST("/v4/ships/query")
        suspend fun fetchShips(
            @Body shipsRequest: Map<String, Any>
        ): ShipsResDto

        @GET("/v4/ships/{id}")
        suspend fun fetchShip(id: String): ShipResDto
    }

    interface Rockets : SpaceXApi {

        @GET("/v4/rockets")
        suspend fun fetchRockets(): List<RocketResDto>?
    }
}
