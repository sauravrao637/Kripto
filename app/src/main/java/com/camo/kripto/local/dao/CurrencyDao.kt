package com.camo.kripto.local.dao

import androidx.room.*
import com.camo.kripto.local.model.Currency
@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCurrencies(currencies: List<Currency>)

    @Query("DELETE FROM Currency")
    suspend fun deleteAllCurrencies()

    @Query("Select * FROM Currency")
    suspend fun getCurrencies(): List<Currency>

    @Query("SELECT COUNT() FROM Currency")
    suspend fun count(): Int

}