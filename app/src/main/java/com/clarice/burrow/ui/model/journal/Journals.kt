package com.clarice.burrow.ui.model.journal

import com.google.gson.annotations.SerializedName

data class Journals(
    @SerializedName("data")
    val data: List<Journal>?
)
