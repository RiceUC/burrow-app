package com.clarice.burrow.ui.model.music

enum class MusicCategory {
    BEFORE_SLEEP,
    DURING_SLEEP
}

data class MusicTrack(
    val id: String,
    val title: String,
    val videoId: String,
    val category: MusicCategory,
    val duration: String
)
