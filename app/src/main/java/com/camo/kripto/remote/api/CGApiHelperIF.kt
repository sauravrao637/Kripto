package com.camo.kripto.remote.api

import com.camo.kripto.local.model.Coin
import com.camo.kripto.local.model.CoinIdName
import com.camo.kripto.remote.model.*

interface CGApiHelperIF {
    //    id_asc, id_desc
    suspend fun getCoins(): ArrayList<Coin>

    suspend fun getSupportedCurr(): ArrayList<String>

    suspend fun getMarketCap(
        curr: String?,
        page: Int,
        order: String?,
        duration: String?,
        ids: List<CoinIdName>?
    ): List<CoinMarket.CoinMarketItem>

    suspend fun getCurrentData(id: String): CoinCD

    suspend fun getMarketChart(id: String, curr: String, days: String): MarketChart

    suspend fun getTrending(): Trending

    suspend fun getGlobal(): Global

    suspend fun getExchanges(page: Int): Exchanges

    suspend fun getGlobalDefi(): GlobalDefi
}