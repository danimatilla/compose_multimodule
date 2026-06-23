package com.dxmxp.domain.model

data class Beer(
    val id: String,
    val name: String,
    val volumeValue: Double? = null,
    val volumeUnit: String? = null,
    val image: String
)
