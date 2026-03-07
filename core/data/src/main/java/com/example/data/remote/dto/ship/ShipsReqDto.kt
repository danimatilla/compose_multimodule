package com.example.data.remote.dto.ship

import kotlinx.serialization.json.Json

data class ShipsReqDto(
    val page: Int = 1
) {
    fun toRequestBody(): Map<String, Any> = """
    {
        "query": {},
        "options": {
            "page": $page 
        }
    }
    """.trimIndent().let { jsonString ->
        Json.decodeFromString<Map<String, Any>>(jsonString)
    }
}
