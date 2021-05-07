package com.camo.kripto.database.dao

import androidx.room.*
import com.camo.kripto.database.model.FavCoin

@Dao
interface FavCoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavCoin(favCoin: FavCoin)


    @Query("DELETE FROM FavCoin WHERE id == :id")
    suspend fun removeFavCoin(id: String)

    @Query("SELECT * FROM FavCoin")
    suspend fun getFavCoins(): List<FavCoin>

    //TODO make suspend
    @Query("SELECT COUNT() FROM FavCoin WHERE id = :id")
    fun count(id: String): Int
}