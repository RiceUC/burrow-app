package com.clarice.burrow.ui.model.music

data class MusicModel (
    val id: Int,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val imageRes: Int
)
