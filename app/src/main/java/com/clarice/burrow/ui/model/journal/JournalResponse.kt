package com.kiara.journal.data.model

import com.google.gson.annotations.SerializedName
data class JournalsResponse(
    @SerializedName("data")
    val data: List<Journal>?
)
