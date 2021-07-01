package com.camo.kripto.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.camo.kripto.local.model.Coin
@Dao
interface CoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCoins(coins: ArrayList<Coin>)

    @Query("DELETE FROM Coin")
    suspend fun deleteAllCoins()

    @Query("Select * FROM Coin")
    suspend fun getCoin(): List<Coin>

    @Query("Select * From Coin Where name like :name ORDER BY id")
    suspend fun getCoinFilterByName(name: String): List<Coin>
}