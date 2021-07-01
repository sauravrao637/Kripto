package com.camo.kripto.remote.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

class CoinMarket : ArrayList<CoinMarket.CoinMarketItem>() {
    data class CoinMarketItem(
        val id: String,
        val symbol: String,
        val name: String,
        val image: String,
        val current_price: BigDecimal,
        val market_cap: BigDecimal,
        val market_cap_rank: Int,
        val fully_diluted_valuation: BigDecimal,
        val total_volume: BigDecimal,
        val high_24h: BigDecimal,
        val low_24h: BigDecimal,
        val price_change_24h: BigDecimal,
        val price_change_percentage_24h: BigDecimal,
        val market_cap_change_24h: BigDecimal,
        val market_cap_change_percentage_24h: BigDecimal,
        val circulating_supply: BigDecimal,
        val total_supply: BigDecimal,
        val max_supply: BigDecimal,
        val ath: BigDecimal,
        val ath_change_percentage: BigDecimal,
        val ath_date: String,
        val atl: BigDecimal,
        val atl_change_percentage: BigDecimal,
        val atl_date: String,
        val roi: Roi,
        val last_updated: String,
        @SerializedName(
            value = "price_change_percentage_1h_in_currency",
            alternate = ["price_change_percentage_24h_in_currency",
                "price_change_percentage_7d_in_currency",
                "price_change_percentage_14d_in_currency",
                "price_change_percentage_30d_in_currency",
                "price_change_percentage_200d_in_currency",
                "price_change_percentage_1y_in_currency"]
        )
        val market_cap_change: BigDecimal?
    ) {

        data class Roi(
            val times: BigDecimal,
            val currency: String,
            val percentage: BigDecimal
        )
    }
}