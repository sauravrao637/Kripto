package com.camo.kripto.data.repository

import com.camo.kripto.data.api.CGApiHelper

class CGrepo(private val cgApiHelper: CGApiHelper) {

    suspend fun getCoins() = cgApiHelper.getCoins()
}