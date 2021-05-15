package com.camo.kripto.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.camo.kripto.local.dao.CurrencyDao
import com.camo.kripto.local.dao.FavCoinDao
import com.camo.kripto.local.model.Currency
import com.camo.kripto.local.model.FavCoin

@Database(entities = [FavCoin::class, Currency::class], version = 2)
//@TypeConverters(DateTypeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun favCoinDao(): FavCoinDao
    abstract fun currencyDao(): CurrencyDao
}