package com.camo.kripto.database.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.camo.kripto.database.AppDb
import com.camo.kripto.database.model.FavCoin

class AppDbRepo(val db:AppDb) {


    suspend fun addFavCoin(favCoin: FavCoin) = db.favCoinDao().addFavCoin(favCoin)



    suspend fun removeFavCoin(id: String) = db.favCoinDao().removeFavCoin(id)


    suspend fun getFavCoins(): List<FavCoin> = db.favCoinDao().getFavCoins()


    fun count(id: String): Int = db.favCoinDao().count(id)
}