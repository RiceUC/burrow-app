package com.kiara.journal.data.model

data class Journal(
    val id: Int,
    val userId: Int,
    val content: String,
    val mood: String,
    val date: String
)

data class JournalRequest(
    val userId: Int,
    val content: String,
    val mood: String,
    val date: String
)
