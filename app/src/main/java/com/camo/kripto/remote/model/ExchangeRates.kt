package com.camo.kripto.remote.model

data class ExchangeRates(
    val rates: Map<String, NVUT>
) {
    data class NVUT(
        val name: String,
        val unit: String,
        val value: Double,
        val type: String
    )
}
