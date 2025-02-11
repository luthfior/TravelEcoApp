package com.example.travelecoapp.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiServiceMidtrans(baseUrl: String): ApiService {
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = req.newBuilder()
                    .addHeader("Authorization", "Basic U0ItTWlkLXNlcnZlci1XRzBiTjFwdkVsckRUSGNLaEpndllQSnk6")
                    .addHeader("x-api-key", "SB-Mid-client-EV6tyZrK3OzoMgJ3")
                    .build()
                chain.proceed(requestHeaders)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
        fun getApiCarbon(): ApiService {
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = req.newBuilder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build()
                chain.proceed(requestHeaders)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://carbon-footprint-emission-jl4gfhbdkq-uc.a.run.app")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // Gunakan client yang baru dibuat
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}