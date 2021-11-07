package com.ddrcss.android.dummydemo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserLocation(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val timezone: String
) : Parcelable
