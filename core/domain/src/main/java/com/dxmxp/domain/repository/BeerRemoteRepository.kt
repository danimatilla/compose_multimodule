package com.dxmxp.domain.repository

import com.dxmxp.domain.model.Beer
import kotlinx.coroutines.flow.Flow

fun interface BeerRemoteRepository {
    fun getBeers(): Flow<List<Beer>>

}