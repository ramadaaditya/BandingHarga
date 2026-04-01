package com.ramstudio.bandingharga.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramstudio.bandingharga.model.StoreType
import com.ramstudio.bandingharga.ui.component.AddStoreSheet
import com.ramstudio.bandingharga.ui.presentation.analytics.LogcatAnalyticsTracker
import com.ramstudio.bandingharga.ui.presentation.dashboard.BandingHargaViewModel
import com.ramstudio.bandingharga.ui.presentation.dashboard.BannerAdPlaceholder
import com.ramstudio.bandingharga.ui.presentation.dashboard.InputContent
import com.ramstudio.bandingharga.ui.presentation.dashboard.ResultContent
import com.ramstudio.bandingharga.ui.presentation.dashboard.ScreenMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandingHargaApp() {
    val analytics = remember { LogcatAnalyticsTracker() }
    val viewModel: BandingHargaViewModel = viewModel(
        factory = BandingHargaViewModel.Factory(analytics)
    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddSheet by rememberSaveable { mutableStateOf(false) }
    var activeTab by rememberSaveable { mutableStateOf(StoreType.ONLINE) }
    var sheetInitialType by rememberSaveable { mutableStateOf(StoreType.ONLINE) }

    if (uiState.showInterstitial) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Iklan") },
            text = { Text(text = "Menampilkan iklan singkat sebelum hasil.") },
            confirmButton = {}
        )
        LaunchedEffect(uiState.showInterstitial) {
            delay(1200)
            viewModel.onInterstitialFinished()
        }
    }

    Scaffold(
        modifier = Modifier.background(Color.Red),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Banding Harga") }
            )
        },
        bottomBar = { BannerAdPlaceholder(modifier = Modifier.navigationBarsPadding()) },
        floatingActionButton = {
            if (uiState.screenMode is ScreenMode.Input) {
                FloatingActionButton(onClick = {
                    sheetInitialType = activeTab
                    showAddSheet = true
                }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Tambah Toko")
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        when (uiState.screenMode) {
            ScreenMode.Input -> InputContent(
                modifier = Modifier.padding(padding),
                stores = uiState.stores,
                activeTab = activeTab,
                onTabChange = { activeTab = it },
                onRemoveStore = viewModel::removeStore,
                onCompare = {
                    if (uiState.stores.size < 2) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Tambahkan minimal 2 toko untuk dibandingkan")
                        }
                    } else {
                        viewModel.requestCompare()
                    }
                }
            )

            ScreenMode.Result -> ResultContent(
                modifier = Modifier.padding(padding),
                results = uiState.results,
                onReset = viewModel::reset
            )
        }
    }

    if (showAddSheet) {
        AddStoreSheet(
            onDismiss = { showAddSheet = false },
            initialType = sheetInitialType,
            onSave = {
                viewModel.addStore(it)
                showAddSheet = false
            }
        )
    }
}