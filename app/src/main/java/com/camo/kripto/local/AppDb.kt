package com.camo.kripto.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.camo.kripto.local.dao.CoinDao
import com.camo.kripto.local.dao.CurrencyDao
import com.camo.kripto.local.dao.FavCoinDao
import com.camo.kripto.local.dao.PriceAlertDao
import com.camo.kripto.local.model.Coin
import com.camo.kripto.local.model.Currency
import com.camo.kripto.local.model.FavCoin
import com.camo.kripto.local.model.PriceAlert

@Database(entities = [FavCoin::class, Currency::class, Coin::class, PriceAlert::class], version = 6)
//@TypeConverters(DateTypeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun favCoinDao(): FavCoinDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun coinDao(): CoinDao
    abstract fun priceAlertDao(): PriceAlertDao
}