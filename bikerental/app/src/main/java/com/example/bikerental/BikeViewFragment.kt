package com.example.bikerental

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var bitmap: Bitmap? = null

/**
 * A simple [Fragment] subclass.
 * Use the [BikeViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BikeViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val view: View = inflater.inflate(R.layout.fragment_bike_view, container, false)

        view.findViewById<LinearLayout>(R.id.bike_view_close).setOnClickListener {
            getActivity()?.onBackPressed();
        }

        val bitmap = textToImage(getArguments()?.get("id").toString(), 500, 500)
        view.findViewById<ImageView>(R.id.bike_view_qr).setImageBitmap(bitmap)
        view.findViewById<ImageView>(R.id.bike_view_qr).updatePadding(0, 0, 0, 0)

        view.findViewById<TextView>(R.id.bike_view_name).text = getArguments()?.get("name").toString()


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BikeViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BikeViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @Throws(WriterException::class, NullPointerException::class)
    private fun textToImage(text: String, width: Int, height: Int): Bitmap? {
        val bitMatrix: BitMatrix
        bitMatrix = try {
            MultiFormatWriter().encode(
                text, BarcodeFormat.QR_CODE,
                width, height, null
            )
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        val colorWhite = -0x1
        val colorBlack = -0x1000000
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) colorBlack else colorWhite
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }
}