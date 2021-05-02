package com.camo.kripto.data.api

import com.camo.kripto.data.model.CoinMarket

class CGApiHelper(private val cgService: CGService) {

//    id_asc, id_desc
    suspend fun getCoins() = cgService.getCoins()
    suspend fun getSupportedCurr() = cgService.getSupportedCurr()
    suspend fun getMarketCap(curr: String, page: Int,order:Int=0): List<CoinMarket.CoinMarketItem> {
        var o = "gecko_asc"
        when(order){
            0 -> o = "market_cap_desc"
            1 -> o = "gecko_desc"
            2 -> o = "gecko_asc"
            3 -> o = "market_cap_asc"
            4 -> o = "market_cap_desc"
            5 -> o = "volume_asc"
            6 -> o = "volume_desc"
        }
        return cgService.getMarketCap(curr, 25, page,o)
    }
}