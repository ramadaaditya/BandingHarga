package com.ramstudio.bandingharga.ui.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.ramstudio.bandingharga.model.StoreInput
import com.ramstudio.bandingharga.model.StoreType
import com.ramstudio.bandingharga.utils.formatRupiah
import com.ramstudio.bandingharga.ui.theme.DiscountGreen
import com.ramstudio.bandingharga.ui.theme.FeeRed

@Composable
fun StoreCard(
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
            HorizontalDivider()
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
            HorizontalDivider()
            StoreValueRow(
                label = "Harga Akhir",
                value = formatRupiah(store.finalPrice),
                valueStyle = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun StoreValueRow(
    label: String,
    value: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    valueStyle: TextStyle = MaterialTheme.typography.bodyLarge
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