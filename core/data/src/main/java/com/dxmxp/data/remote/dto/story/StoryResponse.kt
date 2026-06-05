package com.dxmxp.data.remote.dto.story

import com.dxmxp.domain.model.StoryBo

data class StoryResponse(
    val items: List<StoryBo>,
    val nextToken: String?
)