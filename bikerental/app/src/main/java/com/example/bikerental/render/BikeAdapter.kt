package com.example.bikerental.render

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bikerental.R
import com.example.bikerental.BikeManagerActivity
import com.example.bikerental.BikeViewFragment
import com.example.bikerental.MainActivity
import com.example.bikerental.models.Bike


class BikeAdapter(private val bikeList : ArrayList<Bike>, private val fl_manager: FragmentManager?) : RecyclerView.Adapter<BikeAdapter.BikeViewHolder>() {
    private lateinit var activity: MainActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BikeAdapter.BikeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.bike_card_view,
            parent,
            false
        )

        val activity = MainActivity()

        val vh = BikeViewHolder(itemView)
        val bikeFragment = BikeViewFragment()


        return vh
    }

    override fun onBindViewHolder(holder: BikeAdapter.BikeViewHolder, position: Int) {
        val bike : Bike = bikeList[position]
        holder.bikeRoot.setOnClickListener { it ->
            val bikeViewFragment: Fragment = BikeViewFragment()
            val bundle: Bundle = Bundle()
            bundle.putString("name", bike.name)
            bundle.putString("id", bike.id)
            bikeViewFragment.arguments = bundle
            fl_manager!!.beginTransaction()
                .replace(R.id.fl_wrapper, bikeViewFragment)
                .addToBackStack(null).commit()
        }
        holder.bikeName.text = bike.name
    }

    override fun getItemCount(): Int {
        return bikeList.size
    }

    class BikeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val bikeName : TextView = itemView.findViewById(R.id.bike_name)
        val bikeRoot: LinearLayout = itemView.findViewById(R.id.bike_view_root)

        val bikeStatus : TextView = itemView.findViewById(R.id.bike_status)
        val bikeImg : ImageView = itemView.findViewById(R.id.bike_img)
    }

}