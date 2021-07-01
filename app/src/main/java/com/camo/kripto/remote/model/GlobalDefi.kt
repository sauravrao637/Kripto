package com.camo.kripto.remote.model

import java.math.BigDecimal

data class GlobalDefi(
    val `data`: Data
) {
    data class Data(
        val defi_market_cap: String,
        val eth_market_cap: String,
        val defi_to_eth_ratio: String,
        val trading_volume_24h: String,
        val defi_dominance: String,
        val top_coin_name: String,
        val top_coin_defi_dominance: BigDecimal
    )
}