package edu.uw.minh2804.resift.models

import com.squareup.moshi.Json

data class Article(
	val authors: List<Author>,
	val favicon: String?,
	val publishedDate: String?,
	val title: String?,
	val url: String?,

	@field: Json(name = "summary")
	val description: String?,
)
