package com.dxmxp.ui.mapper

import com.dxmxp.domain.Constants.BASE_IMAGE_URL
import com.dxmxp.domain.di.DispatchersModule.DefaultDispatcher
import com.dxmxp.domain.model.Beer
import com.dxmxp.ui.model.BeerUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BeerUiMapper @Inject constructor(
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    /**
     * Maps a list of domain [Beer] to [BeerUiModel].
     * Executed on [DefaultDispatcher] to avoid blocking the UI thread.
     */
    suspend fun toUiModel(beers: List<Beer>?): List<BeerUiModel> = withContext(defaultDispatcher) {
        beers?.map { it.toUiModel() } ?: emptyList()
    }

    /**
     * Maps a single domain [Beer] to [BeerUiModel].
     */
    fun Beer.toUiModel(): BeerUiModel =
        BeerUiModel(
            id = id,
            name = name,
            volumeText = volumeValue?.let { "$volumeValue $volumeUnit" } ?: "N/A",
            imageUrl = image.takeIf { it.isNotBlank() }?.let { "$BASE_IMAGE_URL$it" }
        )
}
