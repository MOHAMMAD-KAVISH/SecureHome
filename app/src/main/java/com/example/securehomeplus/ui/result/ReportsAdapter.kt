package com.example.securehomeplus.ui.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securehomeplus.databinding.ItemReportBinding
import com.example.securehomeplus.data.database.entities.Report
import java.text.SimpleDateFormat
import java.util.*

class ReportsAdapter : RecyclerView.Adapter<ReportsAdapter.ReportViewHolder>() {

    private val reports = mutableListOf<Report>()

    fun submitList(list: List<Report>) {
        reports.clear()
        reports.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun getItemCount(): Int = reports.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reports[position])
    }

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            binding.tvReportDate.text = dateFormat.format(Date(report.dateMillis))
            binding.tvReportScore.text = "Safety Score: ${report.score}%"
            binding.tvReportSummary.text = report.summary
        }
    }
}
