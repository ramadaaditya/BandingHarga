package com.ramstudio.bandingharga.ui.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ramstudio.bandingharga.model.StoreInput
import com.ramstudio.bandingharga.model.StoreResult
import com.ramstudio.bandingharga.model.StoreType
import com.ramstudio.bandingharga.ui.component.DashboardTabs
import com.ramstudio.bandingharga.ui.component.EmptyStateCard
import com.ramstudio.bandingharga.ui.component.StoreCard
import com.ramstudio.bandingharga.ui.theme.HighlightGold
import com.ramstudio.bandingharga.utils.formatRupiah


@Composable
fun InputContent(
    modifier: Modifier,
    stores: List<StoreInput>,
    activeTab: StoreType,
    onTabChange: (StoreType) -> Unit,
    onRemoveStore: (String) -> Unit,
    onCompare: () -> Unit
) {
    val onlineCount = stores.count { it.type == StoreType.ONLINE }
    val offlineCount = stores.count { it.type == StoreType.OFFLINE }
    val filteredStores = stores.filter { it.type == activeTab }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            DashboardTabs(
                activeTab = activeTab,
                onTabChange = onTabChange,
                onlineCount = onlineCount,
                offlineCount = offlineCount
            )
        }

        if (filteredStores.isEmpty()) {
            item {
                EmptyStateCard()
            }
        } else {
            items(filteredStores, key = { it.id }) { store ->
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
fun ResultContent(
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
fun BannerAdPlaceholder(modifier: Modifier = Modifier) {
    Surface(tonalElevation = 2.dp, modifier = modifier) {
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
