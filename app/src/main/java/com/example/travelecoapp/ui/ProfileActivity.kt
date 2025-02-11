package com.example.travelecoapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.travelecoapp.MainActivity
import com.example.travelecoapp.R
import com.example.travelecoapp.databinding.ActivityProfileBinding
import com.example.travelecoapp.model.AuthViewModel
import com.example.travelecoapp.model.ViewModelFactory
import com.example.travelecoapp.ui.auth.activity.LoginActivity
import com.example.travelecoapp.ui.detail.DetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity(){

    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_profile

        setupModel()

        binding?.btnLogout?.setOnClickListener {
            signOut()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val displayName = sharedPref.getString("displayName", "")
        val email = sharedPref.getString("email", "")

        binding?.tvUser?.text = displayName.toString()
        binding?.tvEmail?.text = email
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
                    startActivity(Intent(this, BucketActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_receipt -> {
                    startActivity(Intent(this, ReceiptActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
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

    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun signOut() {
        authViewModel.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}