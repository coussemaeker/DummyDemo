package com.ddrcss.android.dummydemo.model

import com.google.gson.annotations.SerializedName

data class UsersPage(
    @SerializedName("data")
    val userPreviews: List<UserPreview>,
    @SerializedName("total")
    val totalCount: Int,
    @SerializedName("page")
    val pageIndex: Int,
    val limit: Int
)
