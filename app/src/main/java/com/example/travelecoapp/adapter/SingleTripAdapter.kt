package com.example.travelecoapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelecoapp.database.SinglePrograms
import com.example.travelecoapp.databinding.ItemSingleBinding
import com.example.travelecoapp.ui.detail.SingleActivity
import java.text.NumberFormat
import java.util.Locale

class SingleTripAdapter(private val singleTrips: List<SinglePrograms>) : RecyclerView.Adapter<SingleTripAdapter.SingleProgramsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleProgramsViewHolder {
        val binding = ItemSingleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleProgramsViewHolder(binding)
    }

    override fun getItemCount(): Int = singleTrips.size

    override fun onBindViewHolder(holder: SingleProgramsViewHolder, position: Int) {
        val singleTrip = singleTrips[position]
        val priceToInt = singleTrip.nett_price
        val price = priceToInt?.toInt()
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedPrice = numberFormat.format(price)
        formattedPrice.toString()

        holder.tvProgram.text = singleTrip.activity_name
        holder.tvSubprogram.text = singleTrip.activity_type
        holder.tvDuration.text = singleTrip.duration
        holder.tvInclussion.text = singleTrip.inclussion
        holder.tvLevel.text = singleTrip.level
        holder.tvPriceSingle.text = "Rp $formattedPrice /Pack"
        Glide.with(holder.itemView).load(singleTrip.photo_url).into(holder.ivProgram)
        holder.btnViewPacket.setOnClickListener {
            val intent = Intent(holder.itemView.context, SingleActivity::class.java)
            intent.putExtra("MainProgram", singleTrip.activity_name)
            intent.putExtra("Photo", singleTrip.photo_url)
            intent.putExtra("Program", singleTrip.activity_type)
            intent.putExtra("Details", singleTrip.details)
            intent.putExtra("Duration", singleTrip.duration)
            intent.putExtra("Level", singleTrip.level)
            intent.putExtra("Inclussion", singleTrip.inclussion)
            intent.putExtra("Price", singleTrip.nett_price)
            holder.itemView.context.startActivity(intent)
        }
    }

    inner class SingleProgramsViewHolder(binding: ItemSingleBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvProgram = binding.tvProgram
        val tvSubprogram = binding.tvSubprogram
        val tvDuration = binding.tvDuration
        val tvInclussion = binding.tvInclussion
        val tvLevel = binding.tvLevel
        val tvPriceSingle = binding.tvPriceSingle
        val ivProgram = binding.ivProgram
        val btnViewPacket = binding.btnViewPacket
    }
}