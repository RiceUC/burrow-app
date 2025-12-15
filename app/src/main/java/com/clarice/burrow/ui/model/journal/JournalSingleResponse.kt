package com.clarice.burrow.ui.model.journal

import com.google.gson.annotations.SerializedName

data class JournalSingleResponse(
    @SerializedName("data")
    val data: Journal?
)

