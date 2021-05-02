package com.camo.kripto.data.repository

import com.camo.kripto.data.api.CGApiHelper

class CGRepo(private val cgApiHelper: CGApiHelper) {

    suspend fun getCoins() = cgApiHelper.getCoins()
    suspend fun getSupportedCoins() = cgApiHelper.getSupportedCurr()
}