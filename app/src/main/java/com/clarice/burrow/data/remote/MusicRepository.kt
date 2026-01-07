package com.clarice.burrow.data.remote

import com.clarice.burrow.ui.model.music.MusicTrack

class MusicRepository(private val apiService: ApiService) {

    suspend fun getMusicList(): List<MusicTrack> {
        return try {
            val response = apiService.getMusicList()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.data != null) {
                    body.data
                } else {
                    android.util.Log.w("MusicRepository", "getMusicList: Body or data is null")
                    emptyList()
                }
            } else {
                android.util.Log.e("MusicRepository", "getMusicList failed: ${response.code()} ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "getMusicList exception", e)
            emptyList()
        }
    }
}
