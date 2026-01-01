package com.clarice.burrow.data.repository

import com.clarice.burrow.data.remote.ApiClient
import com.clarice.burrow.ui.model.music.MusicResponse

class MusicRepository {

    suspend fun getAllMusic(): List<MusicResponse> {
        val response = ApiClient.apiService.getAllMusic()
        return response.body()?.data ?: emptyList()
    }
}
