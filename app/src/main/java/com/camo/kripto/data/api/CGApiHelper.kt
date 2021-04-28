package com.camo.kripto.data.api

class CGApiHelper(private val cgService: CGService) {
    suspend fun getCoins() = cgService.getUsers()
}