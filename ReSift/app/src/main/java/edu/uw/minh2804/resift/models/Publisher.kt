package edu.uw.minh2804.resift.models

import com.squareup.moshi.Json

data class Publisher (
    val id: String,
    val name: String,
    val favicon: String?,
    val mbfcUrl: String?,
    val biasRating: Int?,
    val history: String?,

    @field:Json(name = "factualRating")
    val credibilityRating: Int?
)
