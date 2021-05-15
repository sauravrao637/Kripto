package com.camo.kripto.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("name"), Index("id")])
data class FavCoin(
    @PrimaryKey
    override val id: String,
    override val name: String
): CoinIdName
