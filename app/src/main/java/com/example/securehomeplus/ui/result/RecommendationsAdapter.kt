package com.example.securehomeplus.ui.result

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.securehomeplus.R

class RecommendationsAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<RecommendationsAdapter.RecViewHolder>() {

    inner class RecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRecommendation: TextView = itemView.findViewById(R.id.tvRecommendation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommendation, parent, false)
        return RecViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecViewHolder, position: Int) {
        holder.tvRecommendation.text = "• ${items[position]}"
    }

    override fun getItemCount(): Int = items.size
}
