package com.example.securehomeplus.ui.result

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securehomeplus.R
import com.example.securehomeplus.databinding.ActivityViewReportsBinding
import com.example.securehomeplus.viewmodel.ResultViewModel
import com.example.securehomeplus.utils.PreferencesManager

class ViewReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewReportsBinding
    private val viewModel: ResultViewModel by viewModels()
    private lateinit var prefs: PreferencesManager
    private lateinit var adapter: ReportsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar Setup
        setSupportActionBar(binding.toolbarReports)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Your Reports"
        binding.toolbarReports.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        prefs = PreferencesManager(this)
        adapter = ReportsAdapter()
        binding.recyclerReports.layoutManager = LinearLayoutManager(this)
        binding.recyclerReports.adapter = adapter

        val userEmail = prefs.getUserEmail() ?: return

        // Fetch reports from DB
        viewModel.getReports(userEmail) { reports ->
            runOnUiThread {
                if (reports.isEmpty()) {
                    binding.tvNoReports.apply {
                        text = "No reports available 😔"
                        visibility = android.view.View.VISIBLE
                    }
                } else {
                    binding.tvNoReports.visibility = android.view.View.GONE
                    adapter.submitList(reports)
                }
            }
        }
    }
}
