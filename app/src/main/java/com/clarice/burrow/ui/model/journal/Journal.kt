package com.clarice.burrow.ui.model.journal

data class Journal(
    val id: Int,
    val user_id: Int,
    val title: String,
    val content: String,
    val mood: String? = null,
    val tags: List<String>? = null,
    val created_at: String,
    val updated_at: String
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