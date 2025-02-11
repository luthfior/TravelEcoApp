package com.example.travelecoapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelecoapp.database.ListOverNight
import com.example.travelecoapp.databinding.ItemOvernightBinding
import com.example.travelecoapp.ui.detail.OvernightActivity
import java.text.NumberFormat
import java.util.Locale

class OverNightAdapter(private val overNight: List<ListOverNight>) : RecyclerView.Adapter<OverNightAdapter.OverNightViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverNightViewHolder {
        val binding = ItemOvernightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OverNightViewHolder(binding)
    }

    override fun getItemCount(): Int = overNight.size

    override fun onBindViewHolder(holder: OverNightViewHolder, position: Int) {
        val overNight = overNight[position]
        val priceToInt = overNight.nett_price
        val price = priceToInt?.toInt()
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedPrice = numberFormat.format(price)
        formattedPrice.toString()

        holder.tvProgram.text = overNight.package_name
        holder.tvPriceSingle.text = "Rp $formattedPrice /Pack/Nett"
        Glide.with(holder.itemView).load(overNight.photo_url).into(holder.ivProgram)
        holder.btnViewPacket.setOnClickListener {
            val intent = Intent(holder.itemView.context, OvernightActivity::class.java)
            intent.putExtra("Program", overNight.package_name)
            intent.putExtra("Photo", overNight.photo_url)
            intent.putExtra("Duration", overNight.duration)
            intent.putExtra("Description", overNight.description)
            intent.putExtra("Price", overNight.nett_price)
            holder.itemView.context.startActivity(intent)
        }
    }

    inner class OverNightViewHolder(binding: ItemOvernightBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvProgram = binding.tvProgram
        val tvPriceSingle = binding.tvPriceOver
        val ivProgram = binding.ivProgram
        val btnViewPacket = binding.btnViewPacket
    }
}