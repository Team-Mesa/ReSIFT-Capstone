package edu.uw.minh2804.resift.models

data class Publisher (
    val id: String,
    val name: String,
    val favicon: String?,
    val rating: Int,
    val bias: Int,
    val history: String?
)
