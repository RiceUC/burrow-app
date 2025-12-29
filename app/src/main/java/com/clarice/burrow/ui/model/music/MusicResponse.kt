package com.clarice.burrow.ui.model.music

data class MusicResponse(
    val sound_id: Int,
    val title: String,
    val duration: Int?,
    val file_path: String
)