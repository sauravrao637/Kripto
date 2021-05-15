package com.camo.kripto.remote.model

data class Global(
    val `data`: Data
) {
    data class Data(
        val active_cryptocurrencies: Int,
        val upcoming_icos: Int,
        val ongoing_icos: Int,
        val ended_icos: Int,
        val markets: Int,
        val total_market_cap: Map<String, Double>,
        val total_volume: Map<String, Double>,
        val market_cap_percentage: Map<String, Double>,
        val market_cap_change_percentage_24h_usd: Double,
        val updated_at: Int
    )
}
