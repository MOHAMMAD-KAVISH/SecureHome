package com.example.securehomeplus.ui.result

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securehomeplus.R
import com.example.securehomeplus.data.database.entities.Report
import com.example.securehomeplus.databinding.ActivityResultBinding
import com.example.securehomeplus.utils.PreferencesManager
import com.example.securehomeplus.view.map.MapActivity
import com.example.securehomeplus.viewmodel.ResultViewModel
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val viewModel: ResultViewModel by viewModels()
    private lateinit var prefs: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)
        setupToolbar()
        setupUI()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.resultToolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupUI() {
        val score = intent.getIntExtra("score", 0)
        val recommendations = intent.getStringArrayListExtra("recs") ?: arrayListOf()

        // Animate circular progress
        animateProgress(score)

        binding.rvRecommendations.layoutManager = LinearLayoutManager(this)
        binding.rvRecommendations.adapter = RecommendationsAdapter(recommendations)

        binding.btnSave.setOnClickListener {
            val userEmail = prefs.getUserEmail() ?: "unknown"
            val summary = buildSummary(score, recommendations)
            val report = Report(userEmail = userEmail, score = score, summary = summary)
            binding.btnSave.isEnabled = false

            viewModel.saveReport(report) { rowId ->
                runOnUiThread {
                    binding.btnSave.isEnabled = true
                    val msg =
                        if (rowId > 0) "Report saved successfully ✅" else "Failed to save report ❌"
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        binding.btnShare.setOnClickListener {
            val shareText =
                "My Safety Score: $score%\n\nRecommendations:\n${recommendations.joinToString("\n")}"
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(share, "Share your report"))
        }
    }

    private fun animateProgress(target: Int) {
        val animator = ValueAnimator.ofInt(0, target)
        animator.duration = 1200
        animator.addUpdateListener {
            val progress = it.animatedValue as Int
            binding.circular.setProgress(progress, false)
            binding.tvScore.text = "$progress%"
        }
        animator.start()
    }

    private fun buildSummary(score: Int, recs: List<String>): String {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val recShort = recs.joinToString("; ")
        return "Score:$score | Date:$date | Recs:$recShort"
    }
}
