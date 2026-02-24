package com.example.securehomeplus.view.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceFragmentCompat
import com.example.securehomeplus.R
import com.example.securehomeplus.utils.NotificationHelper
import com.example.securehomeplus.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbarSettings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Load the fragment inside the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            //  Notifications Switch
            findPreference<androidx.preference.SwitchPreferenceCompat>("notifications_enabled")
                ?.setOnPreferenceChangeListener { _, newValue ->
                    val enabled = newValue as Boolean
                    if (enabled) NotificationHelper.scheduleDailyReminder(requireContext())
                    else NotificationHelper.cancelReminder(requireContext())
                    true
                }

            //  Theme Selector
            findPreference<androidx.preference.ListPreference>("theme_mode")
                ?.setOnPreferenceChangeListener { _, newValue ->
                    when (newValue) {
                        "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    true
                }

            //  Clear History
            findPreference<androidx.preference.Preference>("clear_history")
                ?.setOnPreferenceClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getDatabase(requireContext()).reportDao().deleteAll()
                    }
                    true
                }
        }
    }
}
