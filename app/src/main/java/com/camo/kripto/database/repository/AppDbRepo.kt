package com.camo.kripto.database.repository

import android.util.Log
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.database.AppDb
import com.camo.kripto.database.model.Currency
import com.camo.kripto.database.model.FavCoin
import com.camo.kripto.utils.Resource
import com.camo.kripto.utils.Status
import timber.log.Timber

class AppDbRepo(val db:AppDb) {



    suspend fun addFavCoin(favCoin: FavCoin) = db.favCoinDao().addFavCoin(favCoin)



    suspend fun removeFavCoin(id: String) = db.favCoinDao().removeFavCoin(id)


    suspend fun getFavCoins(): List<FavCoin> = db.favCoinDao().getFavCoins()


    fun count(id: String): Int = db.favCoinDao().count(id)

    suspend fun addCurrencies(currencies:List<Currency>){
        db.currencyDao().deleteAllCurrencies()
        db.currencyDao().addCurrencies(currencies)
    }

    suspend fun getCurrencies():List<Currency> = db.currencyDao().getCurrencies()
    companion object {
        //loadInRoomCurrencies
        suspend fun lIRcurrencies(repo: AppDbRepo?): Resource<Boolean>{
            val curr = ArrayList<Currency>()
            try
            {
                val strings = CGApiHelper(RetrofitBuilder.CG_SERVICE).getSupportedCurr()
                for (s in strings) {
                    curr.add(Currency(s))
                }
                repo?.addCurrencies(curr)
                return Resource(Status.SUCCESS,true,"success")
            } catch (e: Exception)
            {
                Timber.d( e.message.toString())
                return Resource(Status.ERROR,false,e.message)

            }
        }
    }
}