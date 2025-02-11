package com.example.travelecoapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelecoapp.R
import com.example.travelecoapp.database.ComboPrograms
import com.example.travelecoapp.database.ListOverNight
import com.example.travelecoapp.database.SinglePrograms
import com.example.travelecoapp.ui.detail.ComboActivity
import com.example.travelecoapp.ui.detail.OvernightActivity
import com.example.travelecoapp.ui.detail.SingleActivity
import java.text.NumberFormat
import java.util.Locale

class CombinedTripAdapter(
    private val singleTrips: List<SinglePrograms>,
    private val comboTrips: List<ComboPrograms>,
    private val overnightPackage: List<ListOverNight>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_SINGLE = 0
    private val ITEM_TYPE_COMBO = 1
    private val ITEM_TYPE_OVERNIGHT = 2

    override fun getItemViewType(position: Int): Int {
        return when {
            position < singleTrips.size -> ITEM_TYPE_SINGLE
            position < singleTrips.size + comboTrips.size -> ITEM_TYPE_COMBO
            else -> ITEM_TYPE_OVERNIGHT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_SINGLE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_single, parent, false)
                SingleTripViewHolder(view)
            }
            ITEM_TYPE_COMBO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_combo, parent, false)
                ComboTripViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_overnight, parent, false)
                OverNightViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_SINGLE -> {
                val singleTrip = singleTrips[position]
                val priceToInt = singleTrip.nett_price
                val price = priceToInt?.toInt()
                val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
                val formattedPrice = numberFormat.format(price)
                formattedPrice.toString()

                holder as SingleTripViewHolder
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
            ITEM_TYPE_COMBO -> {
                val comboTrip = comboTrips[position - singleTrips.size]
                val priceToInt = comboTrip.nett_price
                val price = priceToInt?.toInt()
                val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
                val formattedPrice = numberFormat.format(price)
                formattedPrice.toString()

                holder as ComboTripViewHolder
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
            ITEM_TYPE_OVERNIGHT -> {
                val overNight = overnightPackage[position - singleTrips.size - comboTrips.size]
                val priceToInt = overNight.nett_price
                val price = priceToInt?.toInt()
                val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
                val formattedPrice = numberFormat.format(price)
                formattedPrice.toString()

                holder as OverNightViewHolder
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
        }
    }

    override fun getItemCount(): Int {
        return singleTrips.size + comboTrips.size + overnightPackage.size
    }

    class SingleTripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProgram: TextView = itemView.findViewById(R.id.tv_program)
        val tvSubprogram: TextView = itemView.findViewById(R.id.tv_subprogram)
        val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)
        val tvInclussion: TextView = itemView.findViewById(R.id.tv_inclussion)
        val tvLevel: TextView = itemView.findViewById(R.id.tv_level)
        val tvPriceSingle: TextView = itemView.findViewById(R.id.tv_price_single)
        val ivProgram: ImageView = itemView.findViewById(R.id.iv_program)
        val btnViewPacket: Button = itemView.findViewById(R.id.btn_view_packet)
    }

    class ComboTripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProgram: TextView = itemView.findViewById(R.id.tv_program)
        val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)
        val tvInclussion: TextView = itemView.findViewById(R.id.tv_inclussion)
        val tvItem: TextView = itemView.findViewById(R.id.tv_time)
        val priceCombo: TextView = itemView.findViewById(R.id.tv_price_combo)
        val ivProgram: ImageView = itemView.findViewById(R.id.iv_program1)
        val btnViewPacket: Button = itemView.findViewById(R.id.btn_view_packet)
    }

    class OverNightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProgram: TextView =itemView.findViewById(R.id.tv_program)
        val tvPriceSingle: TextView = itemView.findViewById(R.id.tv_price_over)
        val ivProgram: ImageView = itemView.findViewById(R.id.iv_program)
        val btnViewPacket: Button = itemView.findViewById(R.id.btn_view_packet)
    }
}
