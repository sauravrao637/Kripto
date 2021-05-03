package com.camo.kripto.data.api

import com.camo.kripto.data.model.CoinMarket

class CGApiHelper(private val cgService: CGService) {

    //    id_asc, id_desc
    suspend fun getCoins() = cgService.getCoins()
    suspend fun getSupportedCurr() = cgService.getSupportedCurr()
    suspend fun getMarketCap(
        curr: String,
        page: Int,
        order: Int = 0,
        duration: Int
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
        return cgService.getMarketCap(curr, 100, page, o,d)
    }
}