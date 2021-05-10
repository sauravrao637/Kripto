package com.camo.kripto.data.api

import com.camo.kripto.data.model.CoinCD
import com.camo.kripto.data.model.CoinMarket
import com.camo.kripto.data.model.MarketChart
import com.camo.kripto.database.model.CoinIdName

class CGApiHelper(private val cgService: CGService) {

    //    id_asc, id_desc
    suspend fun getCoins() = cgService.getCoins()
    suspend fun getSupportedCurr() = cgService.getSupportedCurr()
    suspend fun getMarketCap(
        curr: String?,
        page: Int,
        order: String?,
        duration: String?,
        ids: List<CoinIdName>?
    ): List<CoinMarket.CoinMarketItem> {

        var s = ""
        if (ids != null && ids.isNotEmpty()) {
            for (i in ids) {
                s += i.id + ","
            }
        }

        return cgService.getMarketCap(
            curr?:"inr",
            25,
            page,
            order ?: "market_cap_desc",
            duration ?: "1h",
            s
        )
    }

    suspend fun getCurrentData(id: String): CoinCD {
        return cgService.getCoinCD(
            id, "true", tickers = false, market_data = true, communityData = false,
            developer_data = false
        )
    }

    suspend fun getMarketChart(id: String, curr: String, days: String): MarketChart {
        return cgService.getCoinMarketChart(id, curr, days)
    }

    suspend fun getTrending() = cgService.getTrending()

    suspend fun getGlobal() = cgService.getGlobal()
}