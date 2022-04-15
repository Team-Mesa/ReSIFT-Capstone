package edu.uw.minh2804.resift.models

import com.squareup.moshi.Json

data class Publisher(
	val history: String?,
	val id: String,
	val mbfcUrl: String?,
	val name: String,

	@field:Json(name = "bias")
	val biasRating: String?,

	@field:Json(name = "factualRating")
	val credibilityRating: Int?,
)
