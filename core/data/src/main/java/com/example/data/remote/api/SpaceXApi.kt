package com.example.data.remote.api

import com.example.data.remote.dto.ship.res.ShipResDto
import com.example.data.remote.dto.ship.res.ShipsResDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SpaceXApi {

    @POST("/v4/ships/query")
    suspend fun fetchShips(
        @Body shipsRequest: Map<String, Any>
    ): ShipsResDto

    @GET("/v4/ships/{id}")
    suspend fun fetchShip(id: String): ShipResDto
}