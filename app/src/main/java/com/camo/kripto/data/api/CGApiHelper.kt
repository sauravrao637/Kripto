package com.camo.kripto.data.api

class CGApiHelper(private val cgService: CGService) {
    suspend fun getCoins() = cgService.getCoins()
    suspend fun getSupportedCurr() = cgService.getSupportedCurr()
    suspend fun getMarketCap(curr: String,page: Int) = cgService.getMarketCap(curr,25,page)
}