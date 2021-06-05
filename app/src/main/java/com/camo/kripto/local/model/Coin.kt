package com.camo.kripto.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("name"), Index("symbol")])
class Coin( @PrimaryKey
            override val id: String,
            override val name: String,
            val symbol: String) : CoinIdName