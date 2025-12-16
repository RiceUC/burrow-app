package com.clarice.burrow.ui.model.music

data class MusicModel(
    val id: Int,
    val title: String,
    val category: String,
    val duration: Int,
    val imageRes: Int
)