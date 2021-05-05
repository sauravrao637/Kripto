package com.camo.kripto.data.repository

import com.camo.kripto.data.api.CGApiHelper

class CGRepo(private val cgApiHelper: CGApiHelper) {

    suspend fun getCoins() = cgApiHelper.getCoins()
    suspend fun getCurrentData(id: String) = cgApiHelper.getCurrentData(id)
    suspend fun getSupportedCurr() = cgApiHelper.getSupportedCurr()
    suspend fun getMarketChart(id: String,curr:String,days: String) = cgApiHelper.getMarketChart(id,curr,days)

}