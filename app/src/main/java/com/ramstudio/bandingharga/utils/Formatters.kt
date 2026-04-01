package com.ramstudio.bandingharga.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatRupiah(value: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    format.currency = Currency.getInstance("IDR")
    format.maximumFractionDigits = 0
    return format.format(value)
}
