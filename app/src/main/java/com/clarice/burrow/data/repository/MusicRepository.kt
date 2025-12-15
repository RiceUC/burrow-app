package com.clarice.burrow.data.repository

import com.clarice.burrow.data.remote.ApiClient
import com.clarice.burrow.ui.model.music.Music

class MusicRepository {

    suspend fun getAllMusic(): List<Music> {
        val response = ApiClient.apiService.getAllMusic()
        if (response.isSuccessful) {
            return response.body()?.data ?: emptyList()
        }
        return emptyList()
    }
}
