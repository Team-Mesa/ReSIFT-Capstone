package edu.uw.minh2804.resift.models

data class Article (
    val title: String?,
    val image: String?,
    val summary: String?,
    val publicationDate: String?,
    val publisher: Publisher?,
    val authors: List<String>,
    val url: String?
)
