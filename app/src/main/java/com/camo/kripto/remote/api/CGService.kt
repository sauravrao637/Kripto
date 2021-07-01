package com.camo.kripto.remote.api

import com.camo.kripto.local.model.Coin
import com.camo.kripto.local.model.PriceAlert
import com.camo.kripto.remote.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.math.BigDecimal

interface CGService {
    @GET("coins/list?include_platform=false")
    suspend fun getCoins(): ArrayList<Coin>

    @GET("simple/supported_vs_currencies")
    suspend fun getSupportedCurr(): ArrayList<String>

    @GET("coins/markets")
    suspend fun getMarketCap(
        @Query("vs_currency") currency: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("order") order: String,
        @Query("price_change_percentage") d: String,
        @Query("ids") ids: String
    ): List<CoinMarket.CoinMarketItem>

    @GET("coins/{id}")
    suspend fun getCoinCD(
        @Path("id") id: String,
        @Query("localization") loc: String,
        @Query("tickers") tickers: Boolean,
        @Query("market_data") market_data: Boolean,
        @Query("community_data") communityData: Boolean,
        @Query("developer_data") developer_data: Boolean,

        ): CoinCD

    @GET("coins/{id}/market_chart")
    suspend fun getCoinMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") curr: String,
        @Query("days") days: String,

        ): Response<MarketChart>

    @GET("search/trending")
    suspend fun getTrending(): Response<Trending>

    @GET("global")
    suspend fun getGlobal(): Global

    @GET("exchanges")
    suspend fun getExchanges(
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Exchanges

    @GET("global/decentralized_finance_defi")
    suspend fun getGlobalDefi(): GlobalDefi

    @GET("ping")
    suspend fun ping(): Response<Any>

    @GET("exchange_rates")
    suspend fun getExchangeRates(): Response<ExchangeRates>

    @GET("status_updates")
    suspend fun getNews(
        @Query("category") category: String,
        @Query("project_type") projectType: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Response<News>

    @GET("simple/price")
    suspend fun getSimplePrice(
        @Query("ids") coinString: String,
        @Query("vs_currencies") currString: String,
        @Query("include_market_cap") includeMC: String,
        @Query("include_24hr_vol") include24hVol: String,
        @Query("include_24hr_change") include24hChange: String,
        @Query("include_last_updated_at") includeLastChanged: String
    ): Response<Map<String, Map<String, BigDecimal>>>


    enum class NewsCategory(val category: String) {
        MILESTONE("milestone"),
        GENERAL("general"),
        PARTNERSHIP("partnership"),
        EXCHANGE_LISTING("exchange_listing"),
        SOFTWARE_RELEASE("software_release"),
        FUND_MOVEMENT("fund_movement"),
        NEW_LISTINGS("new_listings"),
        EVENT("event")
    }
}