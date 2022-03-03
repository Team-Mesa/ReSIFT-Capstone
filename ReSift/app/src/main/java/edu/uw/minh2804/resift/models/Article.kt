package edu.uw.minh2804.resift.models

data class Publisher (
    val id: String,
    val name: String,
    val favicon: String?,
    val rating: Int,
    val bias: Int,
    val history: String?
)

data class Article (
    val title: String?,
    val image: String?,
    val summary: String?,
    val publicationDate: String?,
    val publisher: Publisher?,
    val authors: List<String>?,
)
