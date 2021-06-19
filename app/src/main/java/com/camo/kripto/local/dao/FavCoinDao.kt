package com.camo.kripto.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.camo.kripto.local.model.FavCoin

@Dao
interface FavCoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavCoin(favCoin: FavCoin)

    @Query("DELETE FROM FavCoin WHERE id == :id")
    suspend fun removeFavCoin(id: String)

    @Query("SELECT * FROM FavCoin")
    suspend fun getFavCoins(): List<FavCoin>

    @Query("SELECT COUNT() FROM FavCoin WHERE id = :id")
    suspend fun count(id: String): Int
}