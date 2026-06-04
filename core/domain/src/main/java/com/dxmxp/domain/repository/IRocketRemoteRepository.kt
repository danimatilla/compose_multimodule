package com.dxmxp.domain.repository

import com.dxmxp.domain.model.RocketBo
import kotlinx.coroutines.flow.Flow

interface IRocketRemoteRepository {
    suspend fun fetchRockets(): Flow<List<RocketBo>>
}