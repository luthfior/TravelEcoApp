package com.example.travelecoapp.ui.payment

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.travelecoapp.ui.ReceiptActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MidtransTransactionStatusPolling(
    private val orderId: String,
    private val serverKey: String,
    private val context: Context // Menambahkan Context sebagai parameter
) {

    private val client = OkHttpClient()

    fun checkTransactionStatus(){
        val url = "https://api.midtrans.com/v2/$orderId/status"
        val credential = Credentials.basic(serverKey, "")
        val req = Request.Builder()
            .url(url)
            .header("Authorization", credential)
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("MidtransCallback", "Gagal menyambung MidtransCallback: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("MidtransCallback", "Response dari Midtrans berhasil: ${response.code}")
                    val responseBody = response.body?.string()
                    val transactionStatus = parseTransactionStatus(responseBody)
                    when (transactionStatus) {
                        "settlement" -> {
                            // Status transaksi berhasil (settlement), arahkan pengguna
                            Log.d("MidtransCallback", "Transaksi berhasil!")
                            val intent = Intent(context, ReceiptActivity::class.java)
                            context.startActivity(intent) // Menggunakan context untuk start activity
                        }
                        "pending" -> {
                            // Status transaksi masih pending
                            Log.d("MidtransCallback", "Transaksi masih pending. Coba lagi nanti.")
                        }
                        else -> {
                            // Status transaksi gagal
                            Log.d("MidtransCallback", "Transaksi gagal.")
                        }
                    }
                } else {
                    Log.d("MidtransCallback", "Response dari Midtrans tidak berhasil: ${response.code}")
                }
            }
        })
    }

    private fun parseTransactionStatus(responseBody: String?): String {
        return responseBody?.let { "settlement" } ?: "unknown"
    }
}
