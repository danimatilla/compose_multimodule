package com.dxmxp.data.remote.api

import com.dxmxp.data.remote.dto.rocket.RocketResponse
import retrofit2.http.GET
import retrofit2.http.Path

sealed interface SpaceXApi {

    interface Rockets : SpaceXApi {

        @GET("/{version}/rockets")
        suspend fun fetchRockets(
            @Path("version") version: String = VERSION,
        ): List<RocketResponse>?
    }

    companion object{
        const val VERSION = "v4"
    }
}
