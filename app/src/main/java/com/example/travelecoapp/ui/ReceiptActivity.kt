package com.example.travelecoapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelecoapp.MainActivity
import com.example.travelecoapp.R
import com.example.travelecoapp.adapter.ReceiptAdapter
import com.example.travelecoapp.database.Receipt
import com.example.travelecoapp.databinding.ActivityReceiptBinding
import com.example.travelecoapp.model.ReceiptViewModel
import com.example.travelecoapp.ui.detail.DetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ReceiptActivity : AppCompatActivity() {

    private var _binding: ActivityReceiptBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: ReceiptViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var orderList: ArrayList<Receipt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_receipt

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ReceiptViewModel::class.java]
        orderList = arrayListOf()

        val layoutManager = LinearLayoutManager(this)
        binding?.rvReceipt?.layoutManager = layoutManager
        binding?.rvReceipt?.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        orderList = arrayListOf()
        getOrderData()

        val orderId = intent.getStringExtra("orderId") ?: "Unknown Order ID"
        val transactionId = intent.getStringExtra("transactionId") ?: "Unknown Transaction ID"

        Log.d("ReceiptActivity", "Received Order ID: $orderId")
        Log.d("ReceiptActivity", "Received Transaction ID: $transactionId")
    }

    private fun getOrderData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database = FirebaseDatabase.getInstance().getReference("users").child(userId).child("order")

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orderList.clear() // Clear the list before adding new data
                    for (orderSnapshot in snapshot.children) {
                        val favorite = orderSnapshot.getValue(Receipt::class.java)
                        orderList.add(favorite!!)
                    }
                    binding?.rvReceipt?.adapter?.notifyDataSetChanged()
                    binding?.rvReceipt?.adapter = ReceiptAdapter(orderList)
                    if (orderList.isEmpty()) {
                        binding?.tvEmptyOrder?.visibility = View.VISIBLE
                    } else {
                        binding?.tvEmptyOrder?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ReceiptActivity, resources.getString(R.string.failed), Toast.LENGTH_SHORT).show()
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
                    startActivity(Intent(this, BucketActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_receipt -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }
}