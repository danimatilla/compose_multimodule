package com.dxmxp.domain.repository

import com.dxmxp.domain.model.Rocket
import kotlinx.coroutines.flow.Flow

interface RocketRemoteRepository {
    fun getRockets(): Flow<List<Rocket>>

}