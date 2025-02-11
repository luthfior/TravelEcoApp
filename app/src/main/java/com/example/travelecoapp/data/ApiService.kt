package com.example.travelecoapp.data

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("{orderId}/status")
    fun getOrderDetail(
        @Path("orderId") orderId: String
    ): Call<PaymentResponse>
    @FormUrlEncoded
    @POST("/predict_emission")
    fun getPredictEmission(
        @Field("start_address") startAddress: String
    ): Call<CarbonResponse>
}