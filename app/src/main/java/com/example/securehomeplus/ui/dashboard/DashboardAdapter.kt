package com.example.securehomeplus.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securehomeplus.databinding.ItemDashboardCardBinding

class DashboardAdapter(
    private var items: List<DashboardFeature>,
    private val onClick: (DashboardFeature) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.VH>() {

    inner class VH(private val b: ItemDashboardCardBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(f: DashboardFeature) {
            b.ivIcon.setImageResource(f.iconRes)
            b.tvTitle.text = f.title
            if (f.subtitle.isNullOrBlank()) {
                b.tvSubtitle.visibility = android.view.View.GONE
            } else {
                b.tvSubtitle.visibility = android.view.View.VISIBLE
                b.tvSubtitle.text = f.subtitle
            }
            b.root.setOnClickListener { onClick(f) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDashboardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<DashboardFeature>) {
        items = newItems
        notifyDataSetChanged()
    }
}
