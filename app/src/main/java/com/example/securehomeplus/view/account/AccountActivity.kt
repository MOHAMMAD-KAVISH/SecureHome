package com.example.securehomeplus.view.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.securehomeplus.databinding.ActivityAccountBinding
import com.example.securehomeplus.ui.login.LoginActivity
import com.example.securehomeplus.utils.PreferencesManager
import com.example.securehomeplus.view.help.HelpActivity
import androidx.appcompat.widget.Toolbar
import com.example.securehomeplus.R


class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private lateinit var prefManager: PreferencesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferencesManager(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbarAccount)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val email = prefManager.getUserEmail()
        binding.tvName.text = email ?: "Guest"

        val username = email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
        binding.tvName.text = username
        binding.tvEmail.text = email

        binding.btnHelp.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }



        //  Logout button clears session and navigates to LoginActivity
        binding.btnLogout.setOnClickListener {
            prefManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}
