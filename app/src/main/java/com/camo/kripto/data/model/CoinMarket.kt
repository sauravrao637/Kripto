package com.camo.kripto.data.model

class CoinMarket : ArrayList<CoinMarket.CoinMarketItem>() {
    data class CoinMarketItem(
        val id: String,
        val symbol: String,
        val name: String,
        val image: String,
        val current_price: Double,
        val market_cap: Double,
        val market_cap_rank: Int,
        val fully_diluted_valuation: Double,
        val total_volume: Double,
        val high_24h: Double,
        val low_24h: Double,
        val price_change_24h: Double,
        val price_change_percentage_24h: Double,
        val market_cap_change_24h: Double,
        val market_cap_change_percentage_24h: Double,
        val circulating_supply: Double,
        val total_supply: Double,
        val max_supply: Double,
        val ath: Double,
        val ath_change_percentage: Double,
        val ath_date: String,
        val atl: Double,
        val atl_change_percentage: Double,
        val atl_date: String,
        val roi: Roi,
        val last_updated: String
    )
     {
        data class Roi(
            val times: Double,
            val currency: String,
            val percentage: Double
        )
    }
}