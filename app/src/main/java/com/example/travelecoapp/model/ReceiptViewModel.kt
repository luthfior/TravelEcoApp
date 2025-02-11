package com.example.travelecoapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelecoapp.data.ApiConfig
import com.example.travelecoapp.data.PaymentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReceiptViewModel : ViewModel() {

    private val _orderDetail = MutableLiveData<PaymentResponse>()
    val orderDetail: LiveData<PaymentResponse> = _orderDetail

    fun getOrderDetail(orderId: String = "") {
        val client = ApiConfig.getApiServiceMidtrans("https://api.example.com/v2/").getOrderDetail(orderId)
        client.enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(
                call: Call<PaymentResponse>,
                response: Response<PaymentResponse>
            ) {
                if (response.isSuccessful) {
                    _orderDetail.value = response.body()
                } else {
                    Log.d("DetailView", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                Log.e("DetailView", "onFailure: ${t.message}")
            }
        })
    }
}