package com.dxmxp.data.data_source

import com.dxmxp.data.local.dao.BeerDao
import com.dxmxp.data.local.entity.BeerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface BeerLocalDataSource {
    fun getBeers(): Flow<List<BeerEntity>>
    suspend fun upsertBeers(beers: List<BeerEntity>)
    suspend fun clearBeers()
}

class BeerLocalDataSourceImpl @Inject constructor(
    private val beerDao: BeerDao
) : BeerLocalDataSource {

    override fun getBeers(): Flow<List<BeerEntity>> = beerDao.getBeers()

    override suspend fun upsertBeers(beers: List<BeerEntity>) = beerDao.upsertBeers(beers)

    override suspend fun clearBeers() = beerDao.clearBeers()
}
