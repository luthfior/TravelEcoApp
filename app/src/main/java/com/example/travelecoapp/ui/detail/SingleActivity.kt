package com.example.travelecoapp.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.travelecoapp.R
import com.example.travelecoapp.database.SinglePrograms
import com.example.travelecoapp.databinding.ActivitySingleBinding
import com.example.travelecoapp.ui.payment.PaymentMidtrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

class SingleActivity : AppCompatActivity() {

    private var _binding: ActivitySingleBinding? = null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var singleTrips: ArrayList<SinglePrograms>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySingleBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val mainProgram = intent.getStringExtra("MainProgram")
        val price = intent.getStringExtra("Price")
        val photo = intent.getStringExtra("Photo")

        binding?.btnOrder?.setOnClickListener {
            val intent = Intent(this, PaymentMidtrans::class.java)
            intent.putExtra("Program", mainProgram)
            intent.putExtra("Price", price)
            intent.putExtra("Photo", photo)
            startActivity(intent)
        }

        binding?.btnAddToCart?.setOnClickListener {
            val packageName = intent.getStringExtra("Program")
            val packageDesc = intent.getStringExtra("Inclussion")
            val packagePrice = intent.getStringExtra("Price")
            val packagePhoto = intent.getStringExtra("Photo")

            database = FirebaseDatabase.getInstance().reference

            // Simpan daftar favorit ke Firebase Database
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val userFavoritesRef = database.child("users").child(userId).child("bucket")

                // Membaca daftar favorit saat ini dari Firebase Database
                userFavoritesRef.get().addOnSuccessListener { dataSnapshot ->
                    val favoritesList = mutableListOf<Map<String, String?>>()

                    // Jika data "favorites" sudah ada, tambahkan ke dalam list
                    if (dataSnapshot.exists()) {
                        val currentFavorites = dataSnapshot.value as? List<Map<String, String>>
                        currentFavorites?.let { favoritesList.addAll(it) }
                    }

                    // Buat objek favorit baru
                    val favorite = mapOf(
                        "packageDesc" to packageDesc,
                        "packageName" to packageName,
                        "packagePrice" to packagePrice,
                        "photo_url" to packagePhoto
                    )

                    // Tambahkan objek favorit baru ke dalam list
                    // Tambahkan objek favorit baru ke dalam list jika belum ada
                    if (!favoritesList.any { it["packageName"] == packageName }) {
                        favoritesList.add(favorite)

                        // Simpan list favorit yang sudah diperbarui ke Firebase Database
                        userFavoritesRef.setValue(favoritesList)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, resources.getString(R.string.add_bucket), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, resources.getString(R.string.failed_bucket), Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, resources.getString(R.string.failed_bucket), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        auth = FirebaseAuth.getInstance()
        singleTrips = arrayListOf()
        getSingleData()
    }

    @SuppressLint("SetTextI18n")
    private fun getSingleData() {
        val program = intent.getStringExtra("MainProgram")
        val photo = intent.getStringExtra("Photo")
        val subProgram = intent.getStringExtra("Program")
        val details = intent.getStringExtra("Details")
        val duration = intent.getStringExtra("Duration")
        val level = intent.getStringExtra("Level")
        val inclussion = intent.getStringExtra("Inclussion")
        val price = intent.getStringExtra("Price")
        val priceToInt = price?.toInt()
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedPrice = numberFormat.format(priceToInt)
        formattedPrice.toString()

        binding?.tvProgram?.text = program
        Glide.with(applicationContext)
            .load(photo)
            .into(binding!!.ivProgram)
        binding?.tvSubprogram?.text = subProgram
        binding?.tvDetail?.text = details
        binding?.tvDuration?.text = duration
        binding?.tvLevel?.text = level
        binding?.tvInclussion?.text = inclussion
        binding?.tvPriceSingle?.text = "Rp $formattedPrice /Pack"
    }
}