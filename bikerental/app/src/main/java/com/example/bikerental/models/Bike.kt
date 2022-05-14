package com.example.bikerental.models


import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint


val BIKE_ID_EXTRA = "bikeExtra"

data class Bike(
    val geoinfo: GeoPoint ?= null,
    val name: String ?= null,
    val ownerUID: String ?= null,
    val user: FirebaseUser ?= null,
    var id: String ?= null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("geoinfo"),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(FirebaseUser::class.java.classLoader),
        parcel.readString()
    ) {
    }

    fun <T : Bike?> withId(id: String): T {
        this.id = id
        return this as T
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(ownerUID)
        parcel.writeParcelable(user, flags)
        parcel.writeString(id)
    }

    companion object CREATOR : Parcelable.Creator<Bike> {
        override fun createFromParcel(parcel: Parcel): Bike {
            return Bike(parcel)
        }

        override fun newArray(size: Int): Array<Bike?> {
            return arrayOfNulls(size)
        }
    }
}