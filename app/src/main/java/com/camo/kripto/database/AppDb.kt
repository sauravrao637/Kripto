package com.camo.kripto.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.camo.kripto.database.dao.CurrencyDao
import com.camo.kripto.database.dao.FavCoinDao
import com.camo.kripto.database.model.Currency
import com.camo.kripto.database.model.FavCoin

@Database(entities = [FavCoin::class, Currency::class], version = 2)
//@TypeConverters(DateTypeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun favCoinDao(): FavCoinDao
    abstract fun currencyDao(): CurrencyDao

    companion object {
        var INSTANCE: AppDb? = null

        fun getAppDb(context: Context): AppDb? {
            if (INSTANCE == null) {
                synchronized(AppDb::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDb::class.java,
                        "kriptoDB.db"
                    ).fallbackToDestructiveMigration().
                    build()

                }
            }
            return INSTANCE
        }

        fun destroyDb() {
            INSTANCE = null
        }
    }
}