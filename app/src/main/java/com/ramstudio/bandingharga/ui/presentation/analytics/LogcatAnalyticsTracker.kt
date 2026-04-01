package com.ramstudio.bandingharga.ui.presentation.analytics

import android.util.Log

class LogcatAnalyticsTracker(
    private val tag: String = "BandingHargaAnalytics"
) : AnalyticsTracker {
    override fun trackEvent(name: String, params: Map<String, String>) {
        if (params.isEmpty()) {
            Log.d(tag, "event=$name")
        } else {
            Log.d(tag, "event=$name params=$params")
        }
    }
}
