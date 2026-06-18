package com.dxmxp.domain.repository

import com.dxmxp.domain.common.DataResult
import com.dxmxp.domain.model.Beer
import kotlinx.coroutines.flow.Flow

interface BeerRemoteRepository {
    fun getBeers(shouldReset: Boolean = false): Flow<DataResult<List<Beer>?>>
}
