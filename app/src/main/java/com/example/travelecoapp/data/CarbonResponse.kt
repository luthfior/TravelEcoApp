package com.example.travelecoapp.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarbonResponse(
    @field:SerializedName("predicted_emission")
    val predictedEmission: String? = null
) : Parcelable