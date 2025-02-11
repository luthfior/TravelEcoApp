package com.example.travelecoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelecoapp.adapter.DestinationAdapter
import com.example.travelecoapp.database.Destination
import com.example.travelecoapp.databinding.ActivityMainBinding
import com.example.travelecoapp.ui.BucketActivity
import com.example.travelecoapp.ui.ProfileActivity
import com.example.travelecoapp.ui.ReceiptActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var destinationArrayList: ArrayList<Destination>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_home

        auth = FirebaseAuth.getInstance()

        val layoutManager = LinearLayoutManager(this)
        binding?.rvDestination?.layoutManager = layoutManager
        binding?.rvDestination?.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        destinationArrayList = arrayListOf()
        getDestinationData()

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val displayName = sharedPref.getString("displayName", "")
        val formattedUsername = displayName!!.split(" ").joinToString(" ") { it.replaceFirstChar { name ->
            if (name.isLowerCase()) name.titlecase(
                Locale.getDefault()
            ) else name.toString()
        } }

        database = FirebaseDatabase.getInstance().reference.child("users")
        binding?.tvUsername?.text = formattedUsername
    }

    private fun getDestinationData() {
        database = FirebaseDatabase.getInstance().getReference(DATABASE_NAME)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (destinationSnapshot in snapshot.children) {
                        val destination = destinationSnapshot.getValue(Destination::class.java)
                        destinationArrayList.add(destination!!)
                    }
                    binding?.rvDestination?.adapter = DestinationAdapter(destinationArrayList)
                } else {
                    Log.d(TAG, "No Data")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, resources.getString(R.string.failed), Toast.LENGTH_SHORT).show()
            }
        })
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
        private const val DATABASE_NAME = "destination"
    }
}