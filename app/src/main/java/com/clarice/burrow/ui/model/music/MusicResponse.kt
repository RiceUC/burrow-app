package com.clarice.burrow.ui.model.music

import com.google.gson.annotations.SerializedName

data class MusicResponse(
    @SerializedName("sound_id")
    val sound_id: Int,
    val title: String,
    val duration: Int?,
    val file_path: String
)