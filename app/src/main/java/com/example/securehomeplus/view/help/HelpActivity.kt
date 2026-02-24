package com.example.securehomeplus.view.help

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.securehomeplus.R
import com.example.securehomeplus.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbarHelp)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:mohammadkavish979@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Help Request - SecureHome+")
            }
            startActivity(intent)
        }

        binding.btnFeedback.setOnClickListener {
            showFeedbackDialog()
        }
    }

    private fun showFeedbackDialog() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 30,40, 20)


        val ratingBar = RatingBar(this, null, android.R.attr.ratingBarStyle)
        ratingBar.numStars = 5
        ratingBar.stepSize = 1f

        ratingBar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER_HORIZONTAL }

        val feedbackInput = EditText(this)
        feedbackInput.hint = "Write your feedback here..."
        feedbackInput.setLines(3)
        feedbackInput.gravity = Gravity.TOP

        layout.addView(ratingBar)
        layout.addView(feedbackInput)

        AlertDialog.Builder(this)
            .setTitle("⭐ Rate SecureHome+")
            .setView(layout)
            .setPositiveButton("Submit") { _, _ ->
                val rating = ratingBar.rating
                Toast.makeText(
                    this,
                    "Thanks for rating us $rating stars!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}
