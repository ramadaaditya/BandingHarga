package com.ramstudio.bandingharga.ui.presentation.analytics

interface AnalyticsTracker {
    fun trackEvent(name: String, params: Map<String, String> = emptyMap())
}

class NoOpAnalyticsTracker : AnalyticsTracker {
    override fun trackEvent(name: String, params: Map<String, String>) = Unit
}
