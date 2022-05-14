package com.example.bikerental

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.bikerental.databinding.ActivityBikeManagerBinding
import com.example.bikerental.databinding.ActivityMainBinding
import com.example.bikerental.models.Bike
import com.example.bikerental.repositories.BikeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase

class BikeManagerActivity : AppCompatActivity() {
    private lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBikeManagerBinding.inflate(layoutInflater)

        val bikeRepository = BikeRepository()

        binding.bikeCancel.setOnClickListener {
            onBackPressed()
        }

        user = Firebase.auth.currentUser!!
        if (user != null) {
            // User is signed in
        }


        binding.bikeSave.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.bikeName.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@BikeManagerActivity,
                        "Bike must have a name!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    // TODO: https://medium.com/firebase-developers/patterns-for-security-with-firebase-offload-client-work-to-cloud-functions-7c420710f07
                    val bikeObject = Bike (
                        GeoPoint(51.5439456, 5.4210983),
                        binding.bikeName.text.toString(),
                        user.uid,
                        user
                    )

                    bikeRepository.addNewBike(bikeObject).addOnSuccessListener {
                        Toast.makeText(
                            this@BikeManagerActivity,
                            "Added!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@BikeManagerActivity,
                            "Something went wrong adding your bike!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }

        setContentView(binding.root)
    }
}