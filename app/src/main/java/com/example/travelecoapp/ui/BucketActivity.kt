package com.example.travelecoapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelecoapp.MainActivity
import com.example.travelecoapp.R
import com.example.travelecoapp.adapter.BucketAdapter
import com.example.travelecoapp.database.ListBucket
import com.example.travelecoapp.databinding.ActivityBucketBinding
import com.example.travelecoapp.ui.detail.DetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BucketActivity : AppCompatActivity(){

    private var _binding: ActivityBucketBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var favoriteList: ArrayList<ListBucket>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBucketBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_add_to_cart

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val layoutManager = LinearLayoutManager(this)
        binding?.rvBucket?.layoutManager = layoutManager
        binding?.rvBucket?.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        favoriteList = arrayListOf()
        getFavoriteData()
    }

    private fun getFavoriteData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database = FirebaseDatabase.getInstance().getReference("users").child(userId).child("bucket")
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    favoriteList.clear() // Clear the list before adding new data
                    for (favoriteSnapshot in snapshot.children) {
                        val favorite = favoriteSnapshot.getValue(ListBucket::class.java)
                        favoriteList.add(favorite!!)
                    }
                    binding?.rvBucket?.adapter?.notifyDataSetChanged()
                    binding?.rvBucket?.adapter = BucketAdapter(favoriteList)
                    if (favoriteList.isEmpty()) {
                        binding?.tvEmptyBucket?.visibility = View.VISIBLE
                    } else {
                        binding?.tvEmptyBucket?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@BucketActivity, resources.getString(R.string.failed), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    @Suppress("DEPRECATION")
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, DetailActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_add_to_cart -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_receipt -> {
                    startActivity(Intent(this, ReceiptActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
