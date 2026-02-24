package com.example.securehomeplus.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.securehomeplus.R
import com.example.securehomeplus.databinding.ActivityLoginBinding
import com.example.securehomeplus.ui.dashboard.DashboardActivity
import com.example.securehomeplus.ui.register.RegisterActivity
import com.example.securehomeplus.utils.PreferencesManager
import com.example.securehomeplus.utils.ValidationUtils
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: PreferencesManager

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        prefs = PreferencesManager(this)

        setupBiometric()
        setupClickListeners()
        checkSavedSession()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }

        binding.ivFingerprint.setOnClickListener {
            if (prefs.isBiometricEnabled()) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "Please enable biometric login first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cardFingerprint.setOnClickListener {
            if (prefs.isBiometricEnabled()) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "Please enable biometric login first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBiometric() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val email = prefs.getBiometricUser()
                    if (email != null && email.isNotEmpty()) {
                        prefs.saveLogin(email)
                        navigateToDashboard()
                    } else {
                        Toast.makeText(this@LoginActivity,
                            "No biometric user found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@LoginActivity,
                        "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Log in using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun checkSavedSession() {
        if (prefs.isLoggedIn()) {
            navigateToDashboard()
            return
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        when {
            !ValidationUtils.isValidEmail(email) -> {
                binding.etEmail.error = "Invalid email"
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password required"
            }
            else -> {
                showProgress(true)

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        showProgress(false)
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                if (user.isEmailVerified) {
                                    handleSuccessfulLogin(user.email ?: "")
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Please verify your email first",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    user.sendEmailVerification()
                                }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Login failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    // Line 103-105 ko replace karo isse:
    private fun handleSuccessfulLogin(email: String) {
        prefs.saveLogin(email)

        // Agar remember me checked hai to email save karo
        if (binding.cbRememberMe.isChecked) {  // ✅ FIXED - cbRememberMe use karo
            prefs.saveEmail(email)
        }

        Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
        navigateToDashboard()
    }

    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset Password")

        val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        builder.setView(view)

        val emailInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etResetEmail)

        builder.setPositiveButton("Send Reset Email") { _, _ ->
            val email = emailInput.text.toString().trim()
            if (ValidationUtils.isValidEmail(email)) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        showProgress(true)

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                showProgress(false)
                if (task.isSuccessful) {
                    AlertDialog.Builder(this)
                        .setTitle("Email Sent")
                        .setMessage("Password reset link has been sent to $email")
                        .setPositiveButton("OK", null)
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send reset email: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnLogin.isEnabled = !show
    }
}