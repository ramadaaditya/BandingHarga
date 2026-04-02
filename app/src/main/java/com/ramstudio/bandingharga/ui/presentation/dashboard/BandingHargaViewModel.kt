package com.ramstudio.bandingharga.ui.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramstudio.bandingharga.model.StoreInput
import com.ramstudio.bandingharga.model.StoreResult
import com.ramstudio.bandingharga.model.StoreType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScreenMode {
    data object Input : ScreenMode
    data object Result : ScreenMode
}

data class UiState(
    val screenMode: ScreenMode = ScreenMode.Input,
    val stores: List<StoreInput> = emptyList(),
    val results: List<StoreResult> = emptyList(),
    val showInterstitial: Boolean = false,
    val showAddSheet: Boolean = false,
    val activeTab: StoreType = StoreType.ONLINE,
    val sheetInitialType: StoreType = StoreType.ONLINE,
    val snackbarMessage: String? = null
)

@HiltViewModel
class BandingHargaViewModel @Inject constructor(
) : ViewModel() {

    private val sessionStart = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onFabClick() {
        _uiState.update { state ->
            state.copy(
                showAddSheet = true,
                sheetInitialType = state.activeTab
            )
        }
    }

    fun onDismissAddSheet() {
        _uiState.update { state ->
            state.copy(showAddSheet = false)
        }
    }

    fun onTabChange(type: StoreType) {
        _uiState.update { state ->
            state.copy(activeTab = type)
        }
    }

    fun onStoreSaved(input: StoreInput) {
        _uiState.update { state ->
            state.copy(
                stores = state.stores + input,
                showAddSheet = false
            )
        }
//        analytics.trackEvent(
//            name = "add_store",
//            params = mapOf("type" to input.type.name.lowercase())
//        )
    }

    fun removeStore(id: String) {
        _uiState.update { state ->
            state.copy(stores = state.stores.filterNot { it.id == id })
        }
    }

    fun onCompareClicked() {
        val current = _uiState.value
        if (current.stores.size < 2) {
            _uiState.update { state ->
                state.copy(snackbarMessage = "Tambahkan minimal 2 toko untuk dibandingkan")
            }
            return
        }
        requestCompare()
    }

    private fun requestCompare() {
//        analytics.trackEvent("compare_clicked")
        _uiState.update { state ->
            state.copy(showInterstitial = true)
        }
    }

    fun onSnackbarShown() {
        _uiState.update { state ->
            if (state.snackbarMessage == null) state else state.copy(snackbarMessage = null)
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
//            analytics.trackEvent(
//                name = "session_length",
//                params = mapOf("seconds" to sessionSeconds)
//            )
        }
    }

    fun reset() {
        _uiState.value = UiState()
//        analytics.trackEvent("reset")
    }
}
