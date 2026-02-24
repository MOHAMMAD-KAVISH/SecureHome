package com.example.securehomeplus.ui.survey

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securehomeplus.R
import com.example.securehomeplus.data.database.AppDatabase
import com.example.securehomeplus.data.repository.SecurityRepository
import com.example.securehomeplus.databinding.ActivitySurveyBinding
import com.example.securehomeplus.ui.result.ResultActivity
import com.example.securehomeplus.viewmodel.SecurityViewModel
import com.example.securehomeplus.viewmodel.SecurityViewModelFactory

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var adapter: SurveyAdapter
    private val answers = mutableMapOf<Int, Boolean>()

    private val viewModel: SecurityViewModel by viewModels {
        val dao = try {
            AppDatabase.getDatabase(this).securityDao()
        } catch (e: Exception) {
            null
        }
        SecurityViewModelFactory(SecurityRepository(dao))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Toolbar setup
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        //  Recycler setup
        binding.rvQuestions.layoutManager = LinearLayoutManager(this)

        //  Observe Questions
        viewModel.questions.observe(this) { list ->
            if (list.isNullOrEmpty()) {
                Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show()
                return@observe
            }

            list.forEach { answers[it.id] = false }
            adapter = SurveyAdapter(list) { qId, isChecked ->
                answers[qId] = isChecked
            }
            binding.rvQuestions.adapter = adapter
        }

        //  Loading Observer
        viewModel.loading.observe(this) { loading ->
            binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSubmit.isEnabled = !loading
        }

        //  Submit Button
        binding.btnSubmit.setOnClickListener {
            val qList = viewModel.questions.value ?: emptyList()
            if (qList.isEmpty()) {
                Toast.makeText(this, "No questions found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            qList.forEach { q ->
                if (!answers.containsKey(q.id)) answers[q.id] = false
            }

            Toast.makeText(this, "Evaluating your responses…", Toast.LENGTH_SHORT).show()
            viewModel.evaluateAnswers(qList, answers)
        }

        //  Observe Results
        viewModel.evaluation.observe(this) { result ->
            result?.let {
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("score", it.scorePercent)
                    putStringArrayListExtra("recs", ArrayList(it.recommendations))
                }
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        //  Load Data
        viewModel.loadQuestions()
    }
}
