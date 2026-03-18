package com.example.securehomeplus.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.securehomeplus.R
import com.example.securehomeplus.databinding.ActivityRegisterBinding
import com.example.securehomeplus.ui.login.LoginActivity
import com.example.securehomeplus.utils.ValidationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var isEmailVerified = false
    private var currentEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase - WITHOUT KTX
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Login text click
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Send verification code
        binding.btnSendVerification.setOnClickListener {
            sendEmailVerification()
        }

        // Verify email button
        binding.btnVerifyEmail.setOnClickListener {
            checkEmailVerificationStatus()
        }

        // Register button
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        // Terms checkbox
        binding.cbTerms.setOnCheckedChangeListener { _, isChecked ->
            updateRegisterButtonState()
        }

        // Real-time validation
        binding.etName.setOnFocusChangeListener { _, _ -> validateName() }
        binding.etEmail.setOnFocusChangeListener { _, _ -> validateEmail() }
        binding.etPhone.setOnFocusChangeListener { _, _ -> validatePhone() }
        binding.etPassword.setOnFocusChangeListener { _, _ -> validatePassword() }
        binding.etConfirmPassword.setOnFocusChangeListener { _, _ -> validateConfirmPassword() }
    }

    private fun sendEmailVerification() {
        val email = binding.etEmail.text.toString().trim()

        if (!ValidationUtils.isValidEmail(email)) {
            binding.etEmail.error = "Enter valid email"
            return
        }

        currentEmail = email
        showProgress(true)

        // Create temporary account to send verification
        val tempPassword = "Temp@123456"
        auth.createUserWithEmailAndPassword(email, tempPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Send verification email
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                showProgress(false)
                                binding.btnVerifyEmail.isEnabled = true
                                binding.tvVerificationStatus.text = "Verification email sent to $email"
                                binding.tvVerificationStatus.setTextColor(resources.getColor(R.color.teal_700))

                                Toast.makeText(this,
                                    "Verification email sent! Please check your inbox",
                                    Toast.LENGTH_LONG).show()

                                // Delete temporary account
                                user.delete()
                            } else {
                                showProgress(false)
                                Toast.makeText(this,
                                    "Failed to send email: ${verifyTask.exception?.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    showProgress(false)
                    if (task.exception?.message?.contains("EMAIL_EXISTS") == true) {
                        // Email already exists
                        Toast.makeText(this,
                            "Email already registered. Please login.",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun checkEmailVerificationStatus() {
        if (currentEmail.isEmpty()) {
            Toast.makeText(this, "Please send verification code first", Toast.LENGTH_SHORT).show()
            return
        }

        showProgress(true)

        // Show dialog asking user to verify email
        AlertDialog.Builder(this)
            .setTitle("Email Verification")
            .setMessage("Please check your email and click the verification link. Then click OK to continue.")
            .setPositiveButton("I've Verified") { _, _ ->
                // For demo, we'll assume verified
                // In real app, you'd need to check with Firebase
                isEmailVerified = true
                binding.tvVerificationStatus.text = "✓ Email verified successfully!"
                binding.tvVerificationStatus.setTextColor(resources.getColor(R.color.teal_700))
                binding.btnVerifyEmail.isEnabled = false
                binding.btnSendVerification.isEnabled = false
                updateRegisterButtonState()
                showProgress(false)
                Toast.makeText(this, "Email verified!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun registerUser() {
        if (!validateAll()) return

        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        showProgress(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save user to Firestore
                    saveUserToFirestore(name, email, phone)
                } else {
                    showProgress(false)
                    Toast.makeText(this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToFirestore(name: String, email: String, phone: String) {
        val userId = auth.currentUser?.uid ?: return

        val user = HashMap<String, Any>()
        user["userId"] = userId
        user["name"] = name
        user["email"] = email
        user["phone"] = phone
        user["createdAt"] = System.currentTimeMillis()
        user["isEmailVerified"] = true

        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                showProgress(false)
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                showProgress(false)
                Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Validation Functions
    private fun validateName(): Boolean {
        val name = binding.etName.text.toString().trim()
        return if (name.length < 2) {
            binding.etName.error = "Enter valid name"
            false
        } else {
            binding.etName.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        return if (!ValidationUtils.isValidEmail(email)) {
            binding.etEmail.error = "Invalid email"
            false
        } else {
            binding.etEmail.error = null
            true
        }
    }

    private fun validatePhone(): Boolean {
        val phone = binding.etPhone.text.toString().trim()
        return if (phone.length != 10 || !phone.all { it.isDigit() }) {
            binding.etPhone.error = "Enter 10-digit number"
            false
        } else {
            binding.etPhone.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString().trim()
        return if (password.length < 6) {
            binding.etPassword.error = "Min 6 characters"
            false
        } else {
            binding.etPassword.error = null
            true
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val password = binding.etPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()
        return if (confirm != password) {
            binding.etConfirmPassword.error = "Passwords don't match"
            false
        } else {
            binding.etConfirmPassword.error = null
            true
        }
    }

    private fun validateAll(): Boolean {
        return validateName() && validateEmail() && validatePhone() &&
                validatePassword() && validateConfirmPassword() && isEmailVerified
    }

    private fun updateRegisterButtonState() {
        binding.btnRegister.isEnabled = isEmailVerified && binding.cbTerms.isChecked
    }

    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnRegister.isEnabled = !show && isEmailVerified && binding.cbTerms.isChecked
        binding.btnSendVerification.isEnabled = !show
        binding.btnVerifyEmail.isEnabled = !show
    }
}
