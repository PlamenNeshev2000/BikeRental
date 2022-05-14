package com.example.bikerental.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bikerental.BikeManagerActivity
import com.example.bikerental.BikeViewFragment
import com.example.bikerental.R
import com.example.bikerental.models.Bike
import com.example.bikerental.render.BikeAdapter
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
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var user: FirebaseUser
    private lateinit var bikeArrayList: ArrayList<Bike>
    private lateinit var bikeAdapter: BikeAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        user = Firebase.auth.currentUser!!
        bikeArrayList = arrayListOf()
        bikeAdapter = BikeAdapter(bikeArrayList, fragmentManager)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        view.findViewById<ImageView>(R.id.button_add_bike).setOnClickListener {
            val switchMethodIntent = Intent(requireContext(), BikeManagerActivity::class.java)
            startActivity(switchMethodIntent)
        }

        view.findViewById<ImageView>(R.id.button_rent_bike).setOnClickListener {
            val rentBikeFragment: Fragment = RentBikeFragment()
            requireFragmentManager().beginTransaction()
                .replace(R.id.fl_wrapper, rentBikeFragment)
                .addToBackStack(null).commit()
        }

        var yourBikeRecyclerView = view.findViewById<RecyclerView>(R.id.your_bikes_recycler_view)
        yourBikeRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        yourBikeRecyclerView.adapter = bikeAdapter

        eventChangeListener()

        return view
    }


    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("bike")
            .whereEqualTo("ownerUID", user.uid)
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
                            bikeArrayList.add(dc.document.toObject(Bike::class.java).withId(dc.document.getId()))
                        }
                    }

                    bikeAdapter.notifyDataSetChanged()
                }
            })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}