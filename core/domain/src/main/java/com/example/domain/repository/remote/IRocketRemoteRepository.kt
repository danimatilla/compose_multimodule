package com.example.domain.repository.remote

import com.example.domain.bo.rocket.RocketBo
import kotlinx.coroutines.flow.Flow

interface IRocketRemoteRepository {
    suspend fun fetchRockets(): Flow<List<RocketBo>>
}