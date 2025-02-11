package com.example.travelecoapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.travelecoapp.R
import com.example.travelecoapp.adapter.CombinedTripAdapter
import com.example.travelecoapp.database.ComboPrograms
import com.example.travelecoapp.database.ListOverNight
import com.example.travelecoapp.database.SinglePrograms
import com.example.travelecoapp.databinding.ActivityDetailBinding
import com.example.travelecoapp.databinding.ActivitySearchBinding
import com.google.firebase.database.DatabaseReference
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding

    private lateinit var database: DatabaseReference
    private lateinit var singleTrips: ArrayList<SinglePrograms>
    private lateinit var comboTrips: ArrayList<ComboPrograms>
    private lateinit var overnightPackage: ArrayList<ListOverNight>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val searchView = binding?.inputPackage?.editText

        searchView?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase(Locale.getDefault())
                filterPackages(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


        singleTrips = arrayListOf()
        comboTrips = arrayListOf()
        overnightPackage = arrayListOf()
    }

    private fun filterPackages(query: String) {
        val filteredSingleTrips = singleTrips.filter {
            it.activity_name?.lowercase(Locale.getDefault())?.contains(query) ?: false
        }
        val filteredComboTrips = comboTrips.filter {
            it.activity_name?.lowercase(Locale.getDefault())?.contains(query) ?: false
        }
        val filteredOvernightPackage = overnightPackage.filter {
            it.package_name?.lowercase(Locale.getDefault())?.contains(query) ?: false
        }

        updateRecyclerView(filteredSingleTrips, filteredComboTrips, filteredOvernightPackage)
    }

    private fun updateRecyclerView(
        singleTrips: List<SinglePrograms>,
        comboTrips: List<ComboPrograms>,
        overnightPackage: List<ListOverNight>
    ) {
        val adapter = CombinedTripAdapter(singleTrips, comboTrips, overnightPackage)
        binding?.rvMateri?.adapter = adapter
    }

}