package com.example.securehomeplus.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SecureHomePrefs", Context.MODE_PRIVATE)

    // Login session methods
    fun saveLogin(email: String) {
        val editor = prefs.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("userEmail", email)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("isLoggedIn", false)
    }

    fun getUserEmail(): String? {
        return prefs.getString("userEmail", null)
    }

    // Biometric methods
    fun setBiometricEnabled(enabled: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean("biometricEnabled", enabled)
        editor.apply()
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean("biometricEnabled", false)
    }

    fun saveBiometricUser(email: String) {
        val editor = prefs.edit()
        editor.putString("biometricUser", email)
        editor.apply()
    }

    fun getBiometricUser(): String? {
        return prefs.getString("biometricUser", null)
    }

    // ✅ NEW: Remember Me methods
    fun setRememberMe(enabled: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean("rememberMe", enabled)
        editor.apply()
    }

    fun isRememberMe(): Boolean {
        return prefs.getBoolean("rememberMe", false)
    }

    // ✅ NEW: Save email method (jo error de raha tha)
    fun saveEmail(email: String) {
        val editor = prefs.edit()
        editor.putString("savedEmail", email)
        editor.apply()
    }

    // ✅ NEW: Get saved email method
    fun getSavedEmail(): String? {
        return if (isRememberMe()) prefs.getString("savedEmail", null) else null
    }

    // Logout method
    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()  // Saari preferences clear kar do
        editor.apply()
    }
}