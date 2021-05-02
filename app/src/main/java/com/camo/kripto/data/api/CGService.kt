package com.camo.kripto.data.api

import com.camo.kripto.data.model.Coin
import com.camo.kripto.data.model.CoinMarket
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CGService {
    @GET("coins/list?include_platform=false")
    suspend fun getCoins(): List<Coin.CoinItem>

    @GET("simple/supported_vs_currencies")
    suspend fun getSupportedCurr(): ArrayList<String>

    @GET("coins/markets")
    suspend fun getMarketCap(@Query("vs_currency") currency: String,
                     @Query("per_page") perPage: Int,
                     @Query("page") page:Int): List<CoinMarket.CoinMarketItem>
}