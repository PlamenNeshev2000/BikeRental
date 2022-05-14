package com.example.bikerental.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String,
    val firstname: String,
    val lastname: String
) : Parcelable

