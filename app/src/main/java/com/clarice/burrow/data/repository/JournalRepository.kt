package com.clarice.burrow.data.repository

import android.content.Context
import com.clarice.burrow.data.remote.ApiService
import com.clarice.burrow.data.remote.RetrofitClient
import com.clarice.burrow.ui.model.journal.Journal
import com.clarice.burrow.ui.model.journal.JournalRequest
import com.clarice.burrow.ui.model.journal.JournalUpdateRequest
import android.util.Log

class JournalRepository(context: Context) {
    private val apiService: ApiService = RetrofitClient.getApiService(context)

    suspend fun getJournals(userId: Int): Result<List<Journal>> {
        return try {
            val response = apiService.getJournals(userId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(kotlin.Exception("Failed to fetch journals: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getJournal(journalId: Int): Result<Journal> {
        return try {
            Log.d("JournalRepository", "getJournal called with id=$journalId")

            val response = apiService.getJournal(journalId)

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()?.data
                Log.d("JournalRepository", "Journal loaded: $data")
                if (data != null) Result.success(data) else Result.failure(Exception("Journal not found"))
            } else {
                Result.failure(kotlin.Exception("Failed to load journal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("JournalRepository", "Error: ${e.message}")
            Result.failure(e)
        }
    }


    suspend fun createJournal(request: JournalRequest): Result<Journal> {
        return try {
            val response = apiService.createJournal(request)

            if (response.isSuccessful && response.body() != null) {
                response.body()?.data?.let { Result.success(it) } ?: Result.failure(Exception("Response data is null"))
            } else {
                Result.failure(kotlin.Exception("Failed to create journal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateJournal(journalId: Int, request: JournalUpdateRequest): Result<Journal> {
        return try {
            Log.d("JournalRepository", "updateJournal called with id=$journalId")

            val response = apiService.updateJournal(journalId, request)

            if (response.isSuccessful && response.body() != null) {
                Log.d("JournalRepository", "Update successful")
                response.body()?.data?.let { Result.success(it) } ?: Result.failure(Exception("Response data is null"))
            } else {
                Log.e("JournalRepository", "Update failed: ${response.message()}")
                Result.failure(kotlin.Exception("Failed to update journal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("JournalRepository", "Update exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteJournal(journalId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteJournal(journalId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(kotlin.Exception("Failed to delete journal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}