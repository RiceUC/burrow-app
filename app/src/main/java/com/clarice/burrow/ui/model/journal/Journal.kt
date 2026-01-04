package com.clarice.burrow.ui.model.journal

data class Journal(
    val journal_id: Int,
    val user_id: Int,
    val content: String,
    val mood: String,
    val created_at: String
)

data class JournalRequest(
    val user_id: Int,
    val content: String,
    val mood: String
)

data class JournalUpdateRequest(
    val content: String? = null,
    val mood: String? = null
)
