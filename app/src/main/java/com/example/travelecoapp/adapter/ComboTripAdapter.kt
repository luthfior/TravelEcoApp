package com.example.travelecoapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelecoapp.database.ComboPrograms
import com.example.travelecoapp.databinding.ItemComboBinding
import com.example.travelecoapp.ui.detail.ComboActivity
import java.text.NumberFormat
import java.util.Locale

class ComboTripAdapter(private val comboTrips: List<ComboPrograms>) : RecyclerView.Adapter<ComboTripAdapter.ComboProgramsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboProgramsViewHolder {
        val binding = ItemComboBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComboProgramsViewHolder(binding)
    }

    override fun getItemCount(): Int = comboTrips.size

    override fun onBindViewHolder(holder: ComboProgramsViewHolder, position: Int) {
        val comboTrip = comboTrips[position]
        val priceToInt = comboTrip.nett_price
        val price = priceToInt?.toInt()
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedPrice = numberFormat.format(price)
        formattedPrice.toString()

        holder.tvProgram.text = comboTrip.activity_name
        holder.tvDuration.text = comboTrip.duration
        holder.tvInclussion.text = comboTrip.inclussion
        holder.tvItem.text = comboTrip.start_time
        holder.priceCombo.text = "Rp $formattedPrice"
        Glide.with(holder.itemView).load(comboTrip.photo_url).into(holder.ivProgram)
        holder.btnViewPacket.setOnClickListener {
            val intent = Intent(holder.itemView.context, ComboActivity::class.java)
            intent.putExtra("Program", comboTrip.activity_name)
            intent.putExtra("Photo", comboTrip.photo_url)
            intent.putExtra("Duration", comboTrip.duration)
            intent.putExtra("Inclussion", comboTrip.inclussion)
            intent.putExtra("Time", comboTrip.start_time)
            intent.putExtra("Price", comboTrip.nett_price)
            holder.itemView.context.startActivity(intent)
        }
    }

    inner class ComboProgramsViewHolder(binding: ItemComboBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvProgram = binding.tvProgram
        val tvDuration = binding.tvDuration
        val tvInclussion = binding.tvInclussion
        val tvItem = binding.tvTime
        val priceCombo = binding.tvPriceCombo
        val ivProgram = binding.ivProgram1
        val btnViewPacket = binding.btnViewPacket
    }
}