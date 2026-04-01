package com.ramstudio.bandingharga.analytics

interface AnalyticsTracker {
    fun trackEvent(name: String, params: Map<String, String> = emptyMap())
}

class NoOpAnalyticsTracker : AnalyticsTracker {
    override fun trackEvent(name: String, params: Map<String, String>) = Unit
}
