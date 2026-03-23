package com.example.data.remote.dto.ship

import kotlinx.serialization.json.Json

data class ShipsReqDto(
    val page: Int = 1
) {
    init {
        require(page > 0) { "Page number must be greater than 0" }
    }

    /**
     * The request body for the /v4/ships/query endpoint requires a specific structure.
     * This function constructs that structure as a JSON string and then decodes it into a Map<String, Any>
     * to be used as the request body for the API call.
     *
     * @see <a href="https://github.com/r-spacex/SpaceX-API/blob/master/docs/queries.md">SpaceX API Query Documentation</a>
     */
    fun toRequestBody(): Map<String, Any> = """
    {
        "query": {},
        "options": {
            "page": $page 
        }
    }
    """.trimIndent().let { jsonString ->
        Json.Default.decodeFromString<Map<String, Any>>(jsonString)
    }
}
