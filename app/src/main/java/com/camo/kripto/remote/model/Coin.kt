package com.camo.kripto.remote.model

class Coin : ArrayList<Coin.CoinItem>(){
    data class CoinItem(
        val id: String,
        val name: String,
        val symbol: String
    )
}