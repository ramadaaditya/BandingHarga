package com.ramstudio.bandingharga.ui.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ramstudio.bandingharga.ui.presentation.analytics.AnalyticsTracker
import com.ramstudio.bandingharga.model.StoreInput
import com.ramstudio.bandingharga.model.StoreResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ScreenMode {
    data object Input : ScreenMode
    data object Result : ScreenMode
}

data class UiState(
    val screenMode: ScreenMode = ScreenMode.Input,
    val stores: List<StoreInput> = emptyList(),
    val results: List<StoreResult> = emptyList(),
    val showInterstitial: Boolean = false
)

class BandingHargaViewModel(
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val sessionStart = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun addStore(input: StoreInput) {
        _uiState.update { state ->
            state.copy(stores = state.stores + input)
        }
        analytics.trackEvent(
            name = "add_store",
            params = mapOf("type" to input.type.name.lowercase())
        )
    }

    fun removeStore(id: String) {
        _uiState.update { state ->
            state.copy(stores = state.stores.filterNot { it.id == id })
        }
    }

    fun requestCompare() {
        analytics.trackEvent("compare_clicked")
        _uiState.update { state ->
            state.copy(showInterstitial = true)
        }
    }

    fun onInterstitialFinished() {
        viewModelScope.launch {
            val results = _uiState.value.stores
                .map { StoreResult(it, it.finalPrice) }
                .sortedBy { it.finalPrice }

            _uiState.update { state ->
                state.copy(
                    screenMode = ScreenMode.Result,
                    results = results,
                    showInterstitial = false
                )
            }

            val sessionSeconds = ((System.currentTimeMillis() - sessionStart) / 1000).toString()
            analytics.trackEvent(
                name = "session_length",
                params = mapOf("seconds" to sessionSeconds)
            )
        }
    }

    fun reset() {
        _uiState.value = UiState()
        analytics.trackEvent("reset")
    }

    class Factory(
        private val analytics: AnalyticsTracker
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BandingHargaViewModel(analytics) as T
        }
    }
}
