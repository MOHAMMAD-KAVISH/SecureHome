package com.example.securehomeplus.view.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securehomeplus.databinding.ItemNearbyBinding
import com.example.securehomeplus.data.database.entities.LocationEntity

class NearbyAdapter(
    private val items: List<LocationEntity>,
    private val onClick: (LocationEntity) -> Unit,
    private val distanceProvider: (LocationEntity) -> String
) : RecyclerView.Adapter<NearbyAdapter.VH>() {

    inner class VH(val b: ItemNearbyBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemNearbyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvName.text = item.name
        holder.b.tvType.text = item.type
        holder.b.tvDistance.text = distanceProvider(item)
        holder.b.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
