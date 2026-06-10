package com.dxmxp.data.remote.dto.story

import com.dxmxp.domain.model.Story

data class StoryResponse(
    val items: List<Story>,
    val nextToken: String?
)