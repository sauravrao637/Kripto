package com.camo.kripto.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("name"), Index("id")])
data class FavCoin(
    @PrimaryKey
    val id: String,
    val name: String
)
