package com.clarice.burrow.data.remote

import com.kiara.journal.api.ApiService
import com.kiara.journal.api.RetrofitClient
import com.kiara.journal.data.model.Journal
import com.kiara.journal.data.model.JournalRequest
import com.kiara.journal.data.model.JournalsResponse
import android.util.Log

class JournalRepository {
    private val apiService: ApiService = RetrofitClient.createService()

    suspend fun getJournals(userId: Int): Result<JournalsResponse> {
        return try {
            val response = apiService.getJournals(userId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
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
                Log.d("JournalRepository", "Journal loaded: ${response.body()?.data}")
                Result.success(response.body()!!.data)
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
                Result.success(response.body()!!)
            } else {
                Result.failure(kotlin.Exception("Failed to create journal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateJournal(journalId: Int, request: JournalRequest): Result<Journal> {
        return try {
            Log.d("JournalRepository", "updateJournal called with id=$journalId")

            val response = apiService.updateJournal(journalId, request)

            if (response.isSuccessful && response.body() != null) {
                Log.d("JournalRepository", "Update successful")
                Result.success(response.body()!!)
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
