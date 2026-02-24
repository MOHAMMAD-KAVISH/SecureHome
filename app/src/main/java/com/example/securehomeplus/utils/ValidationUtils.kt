package com.example.securehomeplus.utils

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isNotEmpty(text: String): Boolean {
        return text.trim().isNotEmpty()
    }
}
