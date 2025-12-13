package com.febby.musics.model

data class MusicModel(
    val id: Int,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val imageRes: Int
)