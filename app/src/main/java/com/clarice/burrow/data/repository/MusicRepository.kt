package com.clarice.burrow.data.repository

import com.clarice.burrow.data.remote.ApiService
import com.clarice.burrow.data.remote.NetworkResult
import com.clarice.burrow.data.remote.safeApiCall
import com.clarice.burrow.ui.model.common.ApiResponse
import com.clarice.burrow.ui.model.music.MusicResponse

class MusicRepository(
    private val apiService: ApiService
) {

    suspend fun getAllMusic(): NetworkResult<ApiResponse<List<MusicResponse>>> {
        return safeApiCall { apiService.getAllMusic() }
    }

    suspend fun getMusic(id: Int): NetworkResult<ApiResponse<MusicResponse>> {
        return safeApiCall { apiService.getMusic(id) }
    }
}