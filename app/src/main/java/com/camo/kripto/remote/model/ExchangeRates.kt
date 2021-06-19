package com.camo.kripto.remote.model

import java.math.BigDecimal

data class ExchangeRates(
    val rates: Map<String, NVUT>
) {
    data class NVUT(
        val name: String,
        val unit: String,
        val value: BigDecimal,
        val type: String
    )
}
