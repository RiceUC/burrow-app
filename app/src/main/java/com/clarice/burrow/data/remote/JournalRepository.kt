package com.clarice.burrow.data.remote

import com.clarice.burrow.ui.model.journal.Journal
import com.clarice.burrow.ui.model.journal.JournalRequest
import com.clarice.burrow.ui.model.journal.JournalUpdateRequest
import com.clarice.burrow.ui.model.common.ApiResponse

class JournalRepository(private val apiService: ApiService) {

    suspend fun getJournals(userId: Int): Result<List<Journal>> {
        return try {
            val response = apiService.getJournals(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Journal data is null"))
                }
            } else {
                Result.failure(Exception("Failed to fetch journals: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getJournal(journalId: Int): Result<Journal> {
        return try {
            val response = apiService.getJournal(journalId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Journal data is null"))
                }
            } else {
                Result.failure(Exception("Failed to load journal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createJournal(request: JournalRequest): Result<Journal> {
        return try {
            val response = apiService.createJournal(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Journal data is null"))
                }
            } else {
                Result.failure(Exception("Failed to create journal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateJournal(journalId: Int, request: JournalUpdateRequest): Result<Journal> {
        return try {
            val response = apiService.updateJournal(journalId, request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Journal data is null"))
                }
            } else {
                Result.failure(Exception("Failed to update journal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteJournal(journalId: Int): Result<String> {
        return try {
            val response = apiService.deleteJournal(journalId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Delete response is null"))
                }
            } else {
                Result.failure(Exception("Failed to delete journal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
