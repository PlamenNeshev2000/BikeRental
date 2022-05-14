package com.example.bikerental.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.bikerental.R
import com.example.bikerental.models.Bike

class CustomDialogFragment: DialogFragment() {

    private var bike: Bike? = null

    fun newInstance(username: String?): CustomDialogFragment? {
        val frag = CustomDialogFragment()
        val args = Bundle()
        args.putString("username", username)
        frag.setArguments(args)
        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_custom_dialog, container, false)
        return rootView
    }
}