package com.example.bikerental.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.example.bikerental.R
import com.example.bikerental.models.Bike
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var bikeArrayList: ArrayList<Bike>
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var user: FirebaseUser
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        user = Firebase.auth.currentUser!!
        bikeArrayList = arrayListOf()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment!!.getMapAsync { mMap ->
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear() //clear old markers

            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                val success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.mapstyle
                    )
                )
                if (!success) {
                    Log.e("map", "Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e("map", "Can't find style.", e)
            }

            mMap.moveCamera(CameraUpdateFactory.zoomTo(13f))
            mMap.setMaxZoomPreference(20f)
            mMap.setMinZoomPreference(10f)

            fusedLocationClient!!.lastLocation
                .addOnSuccessListener() { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        val markerOptions = CircleOptions()
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
                        mMap.addCircle(
                            CircleOptions()
                                .center(
                                    LatLng(
                                        location.latitude,
                                        location.longitude
                                    )
                                )
                                .radius(100.0)
                                .strokeWidth(1f)
                                .strokeColor(Color.parseColor("#0034c9"))
                                .fillColor(Color.parseColor("#0063c9"))
                        )
                    }
                }
            db.collection("bike")
                .addSnapshotListener(object: EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null) {
                            Log.e("Firestore error", error.message.toString())
                            return
                        }

                        for (dc: DocumentChange in value?.documentChanges!!){
                            if (dc.type == DocumentChange.Type.ADDED) {
                                bikeArrayList.add(dc.document.toObject(Bike::class.java))
                            }
                        }
                        val b = BitmapFactory.decodeResource(resources, R.raw.bikemarker)
                        val smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false)
                        val bs = BitmapFactory.decodeResource(resources, R.raw.bikemarker_s)
                        val smallMarkerSelf = Bitmap.createScaledBitmap(bs, 100, 100, false)

                        bikeArrayList.forEach {
                            if (it.ownerUID == user.uid) {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(it.geoinfo!!.latitude, it.geoinfo!!.longitude))
                                        .title(it.name)
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerSelf))
                                )!!.setTag(it);
                            } else {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(it.geoinfo!!.latitude, it.geoinfo!!.longitude))
                                        .title(it.name)
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                )!!.setTag(it);
                            }
                        }
                    }
                })

            mMap.setOnMarkerClickListener { marker ->
                Log.d("markerInfo", marker!!.getTag().toString())
                val markerTag : Bike = marker!!.getTag() as Bike
                val bikeOnwer = db.collection("user").document(markerTag.ownerUID!!).get()
                var dialog = Dialog(requireContext())
                if(markerTag.ownerUID != user.uid) {
                    dialog.setContentView(R.layout.fragment_custom_dialog)
                    bikeOnwer.addOnSuccessListener { doc ->
                        dialog.findViewById<TextView>(R.id.fragment_map_bike_owner_name).setText(doc.data!!.get("firstname").toString().plus(" ").plus(doc.data!!.get("lastname").toString()) )
                    }
                } else {
                    dialog.setContentView(R.layout.fragment_custom_dialog_edit_bike)
                }
                dialog.findViewById<TextView>(R.id.fragment_map_bike_title).setText(markerTag.name)

                dialog.show()
                mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(markerTag.geoinfo!!.latitude, markerTag.geoinfo!!.longitude)))
                true
            }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}