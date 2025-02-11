package com.example.travelecoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.travelecoapp.R
import com.example.travelecoapp.database.Receipt
import com.example.travelecoapp.databinding.ItemReceiptBinding
import java.util.Locale

class ReceiptAdapter(private val listReceipt: ArrayList<Receipt>) : RecyclerView.Adapter<ReceiptAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = listReceipt.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val receipt = listReceipt[position]

        holder.orderName.text = receipt.orderName
        holder.orderEmail.text = receipt.orderEmail
        holder.orderId.text = receipt.orderId
        holder.orderPhone.text = receipt.orderEmail
        holder.orderProgram.text = receipt.orderProgram
        holder.orderOfPeople.text = receipt.orderOfPeople
        holder.orderPhone.text = receipt.orderPhone
        holder.orderPrice.text = receipt.orderPrice.toString()
        val status = receipt.orderStatus?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        } ?: "None"
        holder.orderStatus.text = status
        val context = holder.itemView.context
        val statusColor = when (status.lowercase(Locale.getDefault())) {
            "pending" -> ContextCompat.getDrawable(context, R.drawable.rounded_status_pending)
            "success" -> ContextCompat.getDrawable(context, R.drawable.rounded_status_settlement)
            "failed" -> ContextCompat.getDrawable(context, R.drawable.rounded_status_failed)
            "invalid" -> ContextCompat.getDrawable(context, R.drawable.rounded_status_cancel)
            else -> ContextCompat.getDrawable(context, R.drawable.rounded_status_default)
        }

        holder.orderStatus.background = statusColor
    }

    class MyViewHolder(binding: ItemReceiptBinding) : RecyclerView.ViewHolder(binding.root) {
        val orderEmail = binding.tvEmailReceipt
        val orderId = binding.tvOrderId
        val orderName = binding.tvNameReceipt
        val orderProgram = binding.tvProgReceipt
        val orderOfPeople = binding.tvNumReceipt
        val orderPhone = binding.tvPhoneReceipt
        val orderPrice = binding.tvPrice
        val orderStatus = binding.tvStatusOrder
    }
}