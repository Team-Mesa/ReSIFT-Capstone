package edu.uw.minh2804.resift.models

import java.time.LocalDate

data class Article (
    val authors: List<String>,
    val favicon: String,
    val publishDate: LocalDate,
    val snippets: String,
    val title: String
)
