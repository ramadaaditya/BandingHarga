package com.ramstudio.bandingharga.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ramstudio.bandingharga.model.PlatformPreset
import com.ramstudio.bandingharga.model.StoreInput
import com.ramstudio.bandingharga.model.StoreType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoreSheet(
    onDismiss: () -> Unit,
    initialType: StoreType,
    onSave: (StoreInput) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var storeType by rememberSaveable { mutableStateOf(initialType) }

    var selectedPlatform by rememberSaveable { mutableStateOf(PlatformPreset.SHOPEE) }
    var customPlatform by rememberSaveable { mutableStateOf("") }

    var itemName by rememberSaveable { mutableStateOf("") }
    var itemPrice by rememberSaveable { mutableStateOf("") }
    var extraFee by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    val parsedPrice = itemPrice.toLongOrNull() ?: 0L

    val finalPlatformName =
        if (selectedPlatform == PlatformPreset.OTHER) customPlatform else selectedPlatform.label

    val canSave = itemName.isNotBlank() && finalPlatformName.isNotBlank() && parsedPrice > 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Tambah Perbandingan",
                style = MaterialTheme.typography.headlineSmall
            )

            // =========================
            // SECTION: ITEM INFO
            // =========================
            ElevatedCard {
                Column(Modifier.padding(16.dp)) {
                    Text("Info Barang", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Nama Barang") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // =========================
            // SECTION: PLATFORM
            // =========================
            ElevatedCard {
                Column(Modifier.padding(16.dp)) {
                    Text("Platform / Toko", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedPlatform.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Platform") },
                            trailingIcon = {
                                Icon(Icons.Rounded.ArrowDropDown, null)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            PlatformPreset.values().forEach {
                                DropdownMenuItem(
                                    text = { Text(it.label) },
                                    onClick = {
                                        selectedPlatform = it
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedPlatform == PlatformPreset.OTHER) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customPlatform,
                            onValueChange = { customPlatform = it },
                            label = { Text("Nama Platform") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // =========================
            // SECTION: PRICE
            // =========================
            ElevatedCard {
                Column(Modifier.padding(16.dp)) {
                    Text("Harga & Biaya", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = itemPrice,
                        onValueChange = { itemPrice = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga Barang") },
                        prefix = { Text("Rp ") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = extraFee,
                        onValueChange = { extraFee = it.filter { c -> c.isDigit() } },
                        label = { Text("Ongkir / Biaya Tambahan") },
                        prefix = { Text("Rp ") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = discount,
                        onValueChange = { discount = it.filter { c -> c.isDigit() } },
                        label = { Text("Diskon / Voucher") },
                        prefix = { Text("Rp ") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            if (!canSave) {
                Text(
                    text = "Lengkapi nama barang, platform, dan harga.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // ACTION BUTTON
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Batal")
                }

                Button(
                    onClick = {
                        onSave(
                            StoreInput(
                                type = storeType,
                                name = finalPlatformName,
                                itemPrice = parsedPrice,
                                extraFee = extraFee.toLongOrNull() ?: 0L,
                                discount = discount.toLongOrNull() ?: 0L
                            )
                        )
                    },
                    enabled = canSave
                ) {
                    Text("Simpan")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}