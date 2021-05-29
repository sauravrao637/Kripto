package com.camo.kripto.remote.model

import com.camo.kripto.local.model.CoinIdName

data class Trending(val coins: List<Coin>, /* val exchanges: List<Any>*/) {
    data class Coin(
        val item: Item
    ) {
        data class Item(
            override val id: String,
            override val name: String,
            val symbol: String,
            val market_cap_rank: Int,
            val thumb: String,
            val large: String,
            val score: Int
        ) : CoinIdName
    }
}