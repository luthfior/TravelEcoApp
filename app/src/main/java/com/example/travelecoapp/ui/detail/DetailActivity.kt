package com.example.travelecoapp.ui.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.travelecoapp.R
import com.example.travelecoapp.adapter.ComboTripAdapter
import com.example.travelecoapp.adapter.OverNightAdapter
import com.example.travelecoapp.adapter.SingleTripAdapter
import com.example.travelecoapp.database.*
import com.example.travelecoapp.databinding.ActivityDetailBinding
import com.example.travelecoapp.ui.BucketActivity
import com.example.travelecoapp.ui.ProfileActivity
import com.example.travelecoapp.ui.ReceiptActivity
import com.example.travelecoapp.ui.maps.MapsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var singleTrips: ArrayList<SinglePrograms>
    private lateinit var comboTrips: ArrayList<ComboPrograms>
    private lateinit var overnightPackage: ArrayList<ListOverNight>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val displayName = sharedPref.getString("displayName", null) ?: "Guest"
        val formattedUsername = displayName.split(" ").joinToString(" ") { it.replaceFirstChar { name ->
            if (name.isLowerCase()) name.titlecase(
                Locale.getDefault()
            ) else name.toString()
        } }

        database = FirebaseDatabase.getInstance().reference.child("users")
        binding?.tvUsername?.text = formattedUsername
        binding?.searchButton?.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }


        auth = FirebaseAuth.getInstance()
        getDestinationData()
        val layoutManagerListPackage = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerComboPackage = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerOverNightPackage = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.rvPackageSingle?.layoutManager = layoutManagerListPackage
        binding?.rvPackageCombo?.layoutManager = layoutManagerComboPackage
        binding?.rvPackageNight?.layoutManager = layoutManagerOverNightPackage

        binding?.btnLoc?.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_home

        singleTrips = arrayListOf()
        comboTrips = arrayListOf()
        overnightPackage = arrayListOf()
        getListPackage()
        getComboPackage()
        getOvernightPackage()
    }

    private fun getDestinationData() {
        database = FirebaseDatabase.getInstance().getReference(DATABASE_DESTINATION)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (destinationSnapshot in snapshot.children) {
                        val destination = destinationSnapshot.getValue(Destination::class.java)
                        Glide.with(applicationContext)
                            .load(destination?.photo_url)
                            .into(binding!!.ivDestinationDetail)
                        binding?.tvDetail?.text = destination?.name
                        binding?.tvLocation?.text = destination?.location
                        binding?.tvDescription?.text = destination?.shortDescription
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun getListPackage() {
        database = FirebaseDatabase.getInstance().getReference("activity_package").child("single_trip")
        database.get().addOnSuccessListener {
            for (data in it.children) {
                val singleTrip = data.getValue(SinglePrograms::class.java)
                singleTrips.add(singleTrip!!)
            }
            binding?.rvPackageSingle?.adapter = SingleTripAdapter(singleTrips)
        }
    }

    private fun getComboPackage() {
        database = FirebaseDatabase.getInstance().getReference("activity_package").child("combo_trip")
        database.get().addOnSuccessListener {
            for (data in it.children) {
                val comboTrip = data.getValue(ComboPrograms::class.java)
                comboTrips.add(comboTrip!!)
            }
            binding?.rvPackageCombo?.adapter = ComboTripAdapter(comboTrips)
        }
    }

    private fun getOvernightPackage() {
        database = FirebaseDatabase.getInstance().getReference("overnight_package")
        database.get().addOnSuccessListener {
            for (data in it.children) {
                val overnight = data.getValue(ListOverNight::class.java)
                overnightPackage.add(overnight!!)
            }
            binding?.rvPackageNight?.adapter = OverNightAdapter(overnightPackage)
        }
    }

    @Suppress("DEPRECATION")
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_add_to_cart -> {
                    startActivity(Intent(this, BucketActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_receipt -> {
                    startActivity(Intent(this, ReceiptActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    companion object {
        private const val TAG = "DetailActivity"
        private const val DATABASE_DESTINATION = "destination"
    }
}