package com.dxmxp.data.remote.dto.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseErrorResponse(
    @SerialName("message") val message: String? = null,
    @SerialName("error") val error: String? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("status_message") val statusMessage: String? = null
) {
    fun getAnyMessage(): String? = message ?: error ?: reason ?: statusMessage
}
