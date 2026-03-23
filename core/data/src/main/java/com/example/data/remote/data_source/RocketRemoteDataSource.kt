package com.example.data.remote.data_source

import com.example.data.di.CoroutineScopeModule
import com.example.data.di.RemoteModule
import com.example.data.remote.api.SpaceXApi
import com.example.data.remote.dto.rocket.RocketResDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RocketRemoteDataSource @Inject constructor(
    @param:RemoteModule.RocketApi private val api: SpaceXApi.Rockets,
    @param:CoroutineScopeModule.IoDispatcher private val dispatcher: CoroutineDispatcher
) : IRocketRemoteDataSource {

    override suspend fun fetchRockets(): List<RocketResDto>? {
        return withContext(dispatcher) {
            api.fetchRockets()
        }
    }
}

interface IRocketRemoteDataSource {
    suspend fun fetchRockets(): List<RocketResDto>?
}
