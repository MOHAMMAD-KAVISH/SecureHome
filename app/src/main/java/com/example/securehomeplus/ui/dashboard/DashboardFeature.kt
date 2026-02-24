package com.example.securehomeplus.ui.dashboard

data class DashboardFeature(
    val id: Int,
    val title: String,
    val subtitle: String? = null,
    val iconRes: Int,
    val actionType: FeatureAction
)

enum class FeatureAction {
    START_SURVEY,
    VIEW_MAP,
    VIEW_REPORTS,
    OPEN_SETTINGS,
    OPEN_ACCOUNT,
    LOGOUT
}
