package com.camo.kripto.data.repository

import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.model.CoinMarket
import com.camo.kripto.database.model.CoinIdName

class CGRepo(private val cgApiHelper: CGApiHelper) {

    suspend fun getCoins() = cgApiHelper.getCoins()
    suspend fun getCurrentData(id: String) = cgApiHelper.getCurrentData(id)
    suspend fun getSupportedCurr() = cgApiHelper.getSupportedCurr()
    suspend fun getMarketChart(id: String, curr: String, days: String) =
        cgApiHelper.getMarketChart(id, curr, days)

    suspend fun getMarketCap(
        curr: String?,
        page: Int,
        order: String?,
        duration: String?,
        coins: List<CoinIdName>?
    ): List<CoinMarket.CoinMarketItem> = cgApiHelper.getMarketCap(curr,page,order,duration,coins)

    suspend fun getTrending() = cgApiHelper.getTrending()

    suspend fun getGlobal() = cgApiHelper.getGlobal()

    suspend fun getExchanges(page:Int) = cgApiHelper.getExchanges(page)
}