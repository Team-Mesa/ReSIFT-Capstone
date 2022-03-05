package edu.uw.minh2804.resift.models

data class Article (
    val authors: List<Author>,
    val publishedDate: String?,
    val summary: String?,
    val title: String?,
    val url: String?,
)
