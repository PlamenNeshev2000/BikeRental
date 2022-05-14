package com.example.bikerental


import android.Manifest
import android.content.ClipData
import android.content.pm.PackageManager
import com.example.bikerental.fragments.HomeFragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.bikerental.databinding.ActivityMainBinding
import com.example.bikerental.fragments.MapFragment
import com.example.bikerental.fragments.ProfileFragment
import com.example.bikerental.models.Bike
import com.example.bikerental.render.BikeAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.ismaeldivita.chipnavigation.ChipNavigationBar


class MainActivity : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    private val REQUEST_CODE_MAIN_PERMISSIONS = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!hasLocationPermission()) {
            requestPermissions()
        }
        if(!hasCameraPermission()) {
            requestPermissions()
        }

        val mapFragment = MapFragment()
        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()

        if(savedInstanceState == null) {
            findViewById<ChipNavigationBar>(R.id.navigation_bar).setItemSelected(
                R.id.action_home,
                true
            )
            makeCurrentFragment(homeFragment)
        }

        findViewById<ChipNavigationBar>(R.id.navigation_bar).setOnItemSelectedListener { it ->
            when (it) {
                R.id.action_home -> makeCurrentFragment(homeFragment)
                R.id.action_map -> makeCurrentFragment(mapFragment)
                R.id.action_profile -> makeCurrentFragment(profileFragment)
            }
            true
        }


        user = Firebase.auth.currentUser!!
        if (user != null) {
            // User is signed in
        }

        //FirebaseAuth.getInstance().signOut()
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }

    private fun hasLocationPermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if(!hasLocationPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if(!hasCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if(permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_MAIN_PERMISSIONS)
            permissionsToRequest.clear()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_MAIN_PERMISSIONS) {
            for(i in grantResults.indices) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionRequest", "${permissions[i]} granted")
                } else {
                    onBackPressed()
                }
            }
        }
    }
}

