package com.ramstudio.bandingharga.model

import java.util.UUID

enum class StoreType {
    ONLINE,
    OFFLINE
}

data class StoreInput(
    val id: String = UUID.randomUUID().toString(),
    val type: StoreType,
    val name: String,
    val itemPrice: Long,
    val extraFee: Long,
    val discount: Long
) {
    val finalPrice: Long
        get() = (itemPrice + extraFee - discount).coerceAtLeast(0)
}

data class StoreResult(
    val store: StoreInput,
    val finalPrice: Long
)
