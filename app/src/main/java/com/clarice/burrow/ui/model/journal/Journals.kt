package com.kiara.journal.data.model

import com.google.gson.annotations.SerializedName

data class Journals(
    @SerializedName("data")
    val data: List<Journal>?
)
