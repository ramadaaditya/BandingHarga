package com.ramstudio.bandingharga.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramstudio.bandingharga.analytics.LogcatAnalyticsTracker
import com.ramstudio.bandingharga.model.StoreInput
import com.ramstudio.bandingharga.model.StoreResult
import com.ramstudio.bandingharga.model.StoreType
import com.ramstudio.bandingharga.ui.theme.DiscountGreen
import com.ramstudio.bandingharga.ui.theme.FeeRed
import com.ramstudio.bandingharga.ui.theme.HighlightGold
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
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "BandingHarga") }
            )
        },
        bottomBar = { BannerAdPlaceholder() },
        floatingActionButton = {
            if (uiState.screenMode is ScreenMode.Input) {
                FloatingActionButton(onClick = { showAddSheet = true }) {
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
            onSave = {
                viewModel.addStore(it)
                showAddSheet = false
            }
        )
    }
}

@Composable
private fun InputContent(
    modifier: Modifier,
    stores: List<StoreInput>,
    onRemoveStore: (String) -> Unit,
    onCompare: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Masukkan data toko",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Bandingkan harga online dan offline dengan cepat.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (stores.isEmpty()) {
            item {
                EmptyStateCard()
            }
        } else {
            items(stores, key = { it.id }) { store ->
                StoreCard(
                    store = store,
                    onRemove = { onRemoveStore(store.id) }
                )
            }
        }

        item {
            Button(
                onClick = onCompare,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Bandingkan Harga")
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ResultContent(
    modifier: Modifier,
    results: List<StoreResult>,
    onReset: () -> Unit
) {
    val cheapest = results.firstOrNull()
    val runnerUp = results.getOrNull(1)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Hasil Perbandingan",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Urutan dari harga akhir termurah.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (results.isEmpty()) {
            item {
                Text(
                    text = "Belum ada data untuk dibandingkan.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(results, key = { it.store.id }) { result ->
                val isCheapest = cheapest?.store?.id == result.store.id
                ResultCard(
                    result = result,
                    isCheapest = isCheapest,
                    cheapest = cheapest,
                    runnerUp = runnerUp
                )
            }
        }

        item {
            Button(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Hitung Ulang")
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun EmptyStateCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Belum ada toko",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Tap tombol + untuk menambahkan toko online atau offline.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun StoreCard(
    store: StoreInput,
    onRemove: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (store.type == StoreType.ONLINE) Icons.Rounded.Wifi else Icons.Rounded.Storefront,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = if (store.type == StoreType.ONLINE) "Toko Online" else "Toko Offline",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Hapus")
                }
            }
            Divider()
            StoreValueRow(label = "Harga Barang", value = formatRupiah(store.itemPrice))
            StoreValueRow(
                label = if (store.type == StoreType.ONLINE) "Ongkir" else "Transport/Parkir",
                value = formatRupiah(store.extraFee),
                labelColor = FeeRed,
                valueColor = FeeRed
            )
            StoreValueRow(
                label = if (store.type == StoreType.ONLINE) "Diskon/Voucher" else "Diskon Toko",
                value = "- ${formatRupiah(store.discount)}",
                labelColor = DiscountGreen,
                valueColor = DiscountGreen
            )
            Divider()
            StoreValueRow(
                label = "Harga Akhir",
                value = formatRupiah(store.finalPrice),
                valueStyle = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun ResultCard(
    result: StoreResult,
    isCheapest: Boolean,
    cheapest: StoreResult?,
    runnerUp: StoreResult?
) {
    val containerColor = if (isCheapest) HighlightGold else MaterialTheme.colorScheme.surface
    val diffText = if (isCheapest && runnerUp != null) {
        val diff = runnerUp.finalPrice - result.finalPrice
        "Lebih hemat ${formatRupiah(diff)} dibanding ${runnerUp.store.name}"
    } else if (!isCheapest && cheapest != null) {
        val diff = result.finalPrice - cheapest.finalPrice
        "Lebih mahal ${formatRupiah(diff)} dibanding ${cheapest.store.name}"
    } else {
        null
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.store.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                if (isCheapest) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFF6E4E00)
                    )
                }
            }
            Text(
                text = "Harga Akhir: ${formatRupiah(result.finalPrice)}",
                style = MaterialTheme.typography.headlineSmall
            )
            diffText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StoreValueRow(
    label: String,
    value: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor
        )
        Text(
            text = value,
            style = valueStyle,
            color = valueColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddStoreSheet(
    onDismiss: () -> Unit,
    onSave: (StoreInput) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var storeType by rememberSaveable { mutableStateOf(StoreType.ONLINE) }
    var name by rememberSaveable { mutableStateOf("") }
    var itemPrice by rememberSaveable { mutableStateOf("") }
    var extraFee by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }

    val parsedPrice = itemPrice.toLongOrNull() ?: 0L
    val canSave = name.isNotBlank() && parsedPrice > 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tambah Toko",
                style = MaterialTheme.typography.headlineSmall
            )
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = storeType == StoreType.ONLINE,
                    onClick = { storeType = StoreType.ONLINE },
                    shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
                ) {
                    Text(text = "Online")
                }
                SegmentedButton(
                    selected = storeType == StoreType.OFFLINE,
                    onClick = { storeType = StoreType.OFFLINE },
                    shape = RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp)
                ) {
                    Text(text = "Offline")
                }
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = if (storeType == StoreType.ONLINE) "Nama Platform" else "Nama Toko") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = itemPrice,
                onValueChange = { itemPrice = it.filter { char -> char.isDigit() } },
                label = { Text(text = "Harga Barang") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = extraFee,
                onValueChange = { extraFee = it.filter { char -> char.isDigit() } },
                label = { Text(text = if (storeType == StoreType.ONLINE) "Ongkir" else "Transport/Parkir") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = discount,
                onValueChange = { discount = it.filter { char -> char.isDigit() } },
                label = { Text(text = if (storeType == StoreType.ONLINE) "Diskon/Voucher" else "Diskon Toko") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (!canSave) {
                Text(
                    text = "Nama toko dan harga barang wajib diisi.",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Batal")
                }
                Button(
                    onClick = {
                        onSave(
                            StoreInput(
                                type = storeType,
                                name = name.trim(),
                                itemPrice = parsedPrice,
                                extraFee = extraFee.toLongOrNull() ?: 0L,
                                discount = discount.toLongOrNull() ?: 0L
                            )
                        )
                    },
                    enabled = canSave
                ) {
                    Text(text = "Simpan")
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun BannerAdPlaceholder() {
    Surface(tonalElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Banner Ad Placeholder",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
