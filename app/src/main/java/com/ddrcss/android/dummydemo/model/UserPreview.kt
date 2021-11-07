package com.ddrcss.android.dummydemo.model

import com.google.gson.annotations.SerializedName

data class UserPreview(
    val id: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    @SerializedName("picture")
    val pictureUrl: String?
)
