package com.dxmxp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Story(
    val id: String,
    val title: String,
    val description: String,
)
