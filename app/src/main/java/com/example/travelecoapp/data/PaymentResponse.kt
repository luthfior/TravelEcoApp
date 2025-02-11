package com.example.travelecoapp.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentResponse(
	@field:SerializedName("transaction_id")
	val transactionId: String,

	@field:SerializedName("fraud_status")
	val fraudStatus: String,

	@field:SerializedName("status_message")
	val statusMessage: String,

	@field:SerializedName("transaction_status")
	val transactionStatus: String,

	@field:SerializedName("status_code")
	val statusCode: String,

	@field:SerializedName("gross_amount")
	val grossAmount: Double,

	@field:SerializedName("merchant_id")
	val merchantId: String,

	@field:SerializedName("va_numbers")
	val vaNumbers: List<VaNumbersItem>,

	@field:SerializedName("payment_type")
	val paymentType: String,

	@field:SerializedName("transaction_time")
	val transactionTime: String,

	@field:SerializedName("currency")
	val currency: String,

	@field:SerializedName("expiry_time")
	val expiryTime: String,

	@field:SerializedName("order_id")
	val orderId: String
) : Parcelable

@Parcelize
data class VaNumbersItem(
	@field:SerializedName("bank")
	val bank: String,

	@field:SerializedName("va_number")
	val vaNumber: String
) : Parcelable