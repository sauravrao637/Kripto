package com.camo.kripto.data.api

import com.camo.kripto.data.model.CoinCD
import com.camo.kripto.data.model.CoinMarket
import com.camo.kripto.data.model.MarketChart
import com.camo.kripto.database.model.CoinIdName
import com.camo.kripto.database.model.FavCoin

class CGApiHelper(private val cgService: CGService) {

    //    id_asc, id_desc
    suspend fun getCoins() = cgService.getCoins()
    suspend fun getSupportedCurr() = cgService.getSupportedCurr()
    suspend fun getMarketCap(
        curr: String,
        page: Int,
        order: Int = 0,
        duration: Int,
        ids: List<CoinIdName>?
    ): List<CoinMarket.CoinMarketItem> {

        var o = "gecko_asc"
        when (order) {
            0 -> o = "market_cap_desc"
            1 -> o = "gecko_desc"
            2 -> o = "gecko_asc"
            3 -> o = "market_cap_asc"
            4 -> o = "market_cap_desc"
            5 -> o = "volume_asc"
            6 -> o = "volume_desc"
        }

        var d = "1h"
        when (duration) {
            0 -> d = "1h"
            1 -> d = "24h"
            2 -> d = "7d"
            3 -> d = "14d"
            4 -> d = "30d"
            5 -> d = "200d"
            6 -> d = "1y"
        }
        var s: String = ""
        if (ids!=null &&!ids.isEmpty()) {
            for (i in ids) {
                s+=i.id+","
            }
        }
        return cgService.getMarketCap(curr, 100, page, o, d,s)
    }

    suspend fun getCurrentData(id: String): CoinCD {
        return cgService.getCoinCD(
            id, "true", false, true, false,
            false
        )
    }

    suspend fun getMarketChart(id: String, curr: String, days: String): MarketChart {
        return cgService.getCoinMarketChart(id, curr, days)
    }
}