package com.dxmxp.data.remote.api

import com.dxmxp.data.remote.dto.rocket.BeerResponse
import retrofit2.http.GET
import retrofit2.http.Query

sealed interface PunkapiApi {

    interface Beers : PunkapiApi {

        @GET("beers")
        suspend fun fetchBeers(
            @Query("page") page: Int,
            @Query("per_page") limit: Int
        ): List<BeerResponse>?
    }
}
