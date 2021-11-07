package com.ddrcss.android.dummydemo.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFullInfo(
    val id: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val email: String?,
    val dateOfBirth: String,
    val registerDate: String,
    val phone: String?,
    @SerializedName("picture")
    val pictureUrl: String?,
    val location: UserLocation
) : Parcelable
