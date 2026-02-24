package com.example.securehomeplus.ui.dashboard

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.securehomeplus.R
import com.example.securehomeplus.databinding.ActivityDashboardBinding
import com.example.securehomeplus.ui.survey.SurveyActivity
import com.example.securehomeplus.ui.login.LoginActivity
import com.example.securehomeplus.ui.result.ViewReportsActivity
import com.example.securehomeplus.utils.PreferencesManager
import com.example.securehomeplus.view.account.AccountActivity
import com.example.securehomeplus.view.map.MapActivity
import com.example.securehomeplus.view.settings.SettingsActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var adapter: DashboardAdapter
    private lateinit var prefs: PreferencesManager

    // Daily Reminder (kept for background notification)
    private fun scheduleDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, com.example.securehomeplus.service.ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerTime = System.currentTimeMillis() + 60 * 1000L // 1 min for testing
        val interval = android.app.AlarmManager.INTERVAL_DAY
        alarmManager.setInexactRepeating(
            android.app.AlarmManager.RTC_WAKEUP,
            triggerTime,
            interval,
            pendingIntent
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "SecureHome+ Dashboard"

        prefs = PreferencesManager(this)
        scheduleDailyReminder(this)

        // Dynamic welcome text
        val userName = prefs.getUserEmail()?.substringBefore("@") ?: "Kavish"
        binding.tvWelcome.text = "Welcome, $userName 👋"

        setupRecycler()
        setupFab()

        // Menu click listener
        binding.toolbar.setOnMenuItemClickListener { item ->
            onOptionsItemSelected(item)
        }

        // Footer quote
        binding.tvQuote.text = "“Your home’s safety is our priority 🔒”"
        binding.tvCopyright.text = "© 2025 SecureHome+ | Crafted by Kavish 💚"
    }

    private fun setupRecycler() {
        val features = listOf(
            DashboardFeature(1, "Start Safety Evaluation", "Quick home check", R.drawable.ic_survey, FeatureAction.START_SURVEY),
            DashboardFeature(2, "View Nearby Points", "Police & Fire", R.drawable.ic_map, FeatureAction.VIEW_MAP),
            DashboardFeature(3, "View Reports", "Saved checks", R.drawable.ic_reports, FeatureAction.VIEW_REPORTS),
            DashboardFeature(4, "Settings", "Preferences", R.drawable.ic_settings, FeatureAction.OPEN_SETTINGS),
            DashboardFeature(5, "My Account", "Profile", R.drawable.ic_person, FeatureAction.OPEN_ACCOUNT),
            DashboardFeature(6, "Logout", "Sign out", R.drawable.ic_logout, FeatureAction.LOGOUT)
        )

        adapter = DashboardAdapter(features) { feature -> handleFeatureClick(feature) }

        binding.rvFeatures.layoutManager = GridLayoutManager(this, 2)
        binding.rvFeatures.adapter = adapter

        val spacing = (resources.displayMetrics.density * 8).toInt()
        binding.rvFeatures.addItemDecoration(SpaceItemDecoration(spacing))
    }

    private fun setupFab() {
        binding.fabNewSurvey.setOnClickListener {
            startActivity(Intent(this, SurveyActivity::class.java))
        }
    }

    private fun handleFeatureClick(feature: DashboardFeature) {
        when (feature.actionType) {
            FeatureAction.START_SURVEY -> startActivity(Intent(this, SurveyActivity::class.java))
            FeatureAction.VIEW_MAP -> startActivity(Intent(this, MapActivity::class.java))
            FeatureAction.VIEW_REPORTS -> startActivity(Intent(this, ViewReportsActivity::class.java))
            FeatureAction.OPEN_SETTINGS -> startActivity(Intent(this, SettingsActivity::class.java))
            FeatureAction.OPEN_ACCOUNT -> startActivity(Intent(this, AccountActivity::class.java))
            FeatureAction.LOGOUT -> performLogout()
        }
    }

    private fun performLogout() {
        prefs.clearSession()
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        val i = Intent(this, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_account -> {
                startActivity(Intent(this, AccountActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
