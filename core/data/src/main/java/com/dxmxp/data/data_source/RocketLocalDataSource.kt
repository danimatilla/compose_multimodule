package com.dxmxp.data.data_source

import com.dxmxp.data.local.dao.RocketDao
import com.dxmxp.data.local.entity.RocketEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RocketLocalDataSource {
    fun getRockets(): Flow<List<RocketEntity>>
    suspend fun upsertRockets(rockets: List<RocketEntity>)
    suspend fun clearRockets()
}

class RocketLocalDataSourceImpl @Inject constructor(
    private val rocketDao: RocketDao
) : RocketLocalDataSource {

    override fun getRockets(): Flow<List<RocketEntity>> = rocketDao.getRockets()

    override suspend fun upsertRockets(rockets: List<RocketEntity>) = rocketDao.upsertRockets(rockets)

    override suspend fun clearRockets() = rocketDao.clearRockets()
}
