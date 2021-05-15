package com.camo.kripto.remote.api

import com.camo.kripto.remote.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CGService {
    @GET("coins/list?include_platform=false")
    suspend fun getCoins(): List<Coin.CoinItem>

    @GET("simple/supported_vs_currencies")
    suspend fun getSupportedCurr(): ArrayList<String>

    @GET("coins/markets")
    suspend fun getMarketCap(
        @Query("vs_currency") currency: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("order") order: String,
        @Query("price_change_percentage")d: String,
        @Query("ids") ids: String
    ): List<CoinMarket.CoinMarketItem>

    @GET("coins/{id}")
    suspend fun getCoinCD(
        @Path("id")id : String,
        @Query("localization") loc: String,
        @Query("tickers") tickers: Boolean,
        @Query("market_data") market_data: Boolean,
        @Query("community_data") communityData: Boolean,
        @Query("developer_data") developer_data: Boolean,

    ): CoinCD

    @GET("coins/{id}/market_chart")
    suspend fun getCoinMarketChart(
        @Path("id") id:String,
        @Query("vs_currency") curr: String,
        @Query("days")days: String,

    ): MarketChart

    @GET("search/trending")
    suspend fun getTrending(): Trending

    @GET("global")
    suspend fun getGlobal() :Global

    @GET("exchanges")
    suspend fun getExchanges(
        @Query("per_page")perPage: Int,
        @Query("page")page:Int
    ): Exchanges

    @GET("global/decentralized_finance_defi")
    suspend fun getGlobalDefi(): GlobalDefi
}