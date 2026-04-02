package com.ramstudio.bandingharga.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ramstudio.bandingharga.ui.component.AddStoreSheet
import com.ramstudio.bandingharga.ui.presentation.dashboard.BandingHargaViewModel
import com.ramstudio.bandingharga.ui.presentation.dashboard.BannerAdPlaceholder
import com.ramstudio.bandingharga.ui.presentation.dashboard.InputContent
import com.ramstudio.bandingharga.ui.presentation.dashboard.ResultContent
import com.ramstudio.bandingharga.ui.presentation.dashboard.ScreenMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandingHargaApp(
    viewModel: BandingHargaViewModel = hiltViewModel()
) {
//    val analytics = remember { LogcatAnalyticsTracker() }
//    val viewModel: BandingHargaViewModel = viewModel(
//        factory = BandingHargaViewModel.Factory(analytics)
//    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = androidx.compose.material3.rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    /*
    Focus on global state
     */

    if (uiState.showInterstitial) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Iklan") },
            text = { Text(text = "Menampilkan iklan singkat sebelum hasil.") },
            confirmButton = {}
        )
        LaunchedEffect(Unit) {
            delay(1200)
            viewModel.onInterstitialFinished()
        }
    }

    val message = uiState.snackbarMessage
    if (message != null) {
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.onSnackbarShown()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Fitur",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    label = { Text(text = "History perbandingan harga") },
                    selected = false,
                    icon = { Icon(Icons.Rounded.History, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        scope.launch { snackbarHostState.showSnackbar("Fitur belum tersedia") }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.background(Color.Red),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Banding Harga") },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Buka menu")
                        }
                    }
                )
            },
            bottomBar = { BannerAdPlaceholder(modifier = Modifier.navigationBarsPadding()) },
            floatingActionButton = {
                if (uiState.screenMode is ScreenMode.Input) {
                    FloatingActionButton(onClick = viewModel::onFabClick) {
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
                    activeTab = uiState.activeTab,
                    onTabChange = viewModel::onTabChange,
                    onRemoveStore = viewModel::removeStore,
                    onCompare = viewModel::onCompareClicked
                )

                ScreenMode.Result -> ResultContent(
                    modifier = Modifier.padding(padding),
                    results = uiState.results,
                    onReset = viewModel::reset
                )
            }
        }
    }

    if (uiState.showAddSheet) {
        AddStoreSheet(
            onDismiss = viewModel::onDismissAddSheet,
            initialType = uiState.sheetInitialType,
            onSave = {
                viewModel.onStoreSaved(it)
            }
        )
    }
}
