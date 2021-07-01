package com.camo.kripto.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")])
class PriceAlert(
    override val id: String,
    override val name: String,
    val curr: String,
    val lessThan: String,
    val moreThan: String,
    val enabled: Boolean,
    val shown: Boolean,
    val isTriggerOnceOnly: Boolean
) : CoinIdName {
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Long = 0
}