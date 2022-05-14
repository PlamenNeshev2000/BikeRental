package com.example.bikerental.repositories

import android.util.Log
import android.widget.Toast
import com.example.bikerental.models.Bike
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class BikeRepository() {
    private val db = FirebaseFirestore.getInstance()
    private val user = Firebase.auth.currentUser!!

    object Tags {
        const val BIKE_PERSIST_ERROR = "BIKE_PERSIST_ERROR"
    }

    public fun addNewBike(bike: Bike): Task<Void> {
        val bikeMap = HashMap<String, Any>()
        bikeMap["name"] = bike.name!!
        bikeMap["geoinfo"] = bike.geoinfo!!
        bikeMap["ownerUID"] = user.uid

        return db.collection("bike").document()
            .set(bikeMap).addOnFailureListener { e ->
                Log.d(
                    Tags.BIKE_PERSIST_ERROR,
                    "There was an error creating a bike object in bike repository!",
                    e
                )
            }
    }
}