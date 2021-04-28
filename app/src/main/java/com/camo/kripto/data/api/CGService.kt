package com.camo.kripto.data.api

import com.camo.kripto.data.model.Coin
import retrofit2.http.GET

interface CGService {
    @GET("coins/list?include_platform=false")
    suspend fun getUsers(): List<Coin.CoinItem>

}