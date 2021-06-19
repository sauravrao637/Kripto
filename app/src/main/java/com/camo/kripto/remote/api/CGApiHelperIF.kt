package com.camo.kripto.remote.api

import com.camo.kripto.local.model.Coin
import com.camo.kripto.local.model.CoinIdName
import com.camo.kripto.local.model.PriceAlert
import com.camo.kripto.remote.model.*
import retrofit2.Response
import java.math.BigDecimal

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

    suspend fun getMarketChart(id: String, curr: String, days: String): Response<MarketChart>

    suspend fun getTrending(): Response<Trending>

    suspend fun getGlobal(): Global

    suspend fun getExchanges(page: Int): Exchanges

    suspend fun getGlobalDefi(): GlobalDefi

    suspend fun ping(): Response<Any>

    suspend fun getExchangeRates(): Response<ExchangeRates>

    suspend fun getNews(category: String, projectType: String, page: Int): Response<News>

    suspend fun getSimplePrice(coinIds: List<String>, currencies: List<String>): Response<Map<String, Map<String, BigDecimal>>>
}