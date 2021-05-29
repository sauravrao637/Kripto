package com.camo.kripto.repos

import com.camo.kripto.local.AppDb
import com.camo.kripto.local.model.Coin
import com.camo.kripto.local.model.CoinIdName
import com.camo.kripto.local.model.Currency
import com.camo.kripto.local.model.FavCoin
import com.camo.kripto.remote.api.CGApiHelper
import com.camo.kripto.remote.model.CoinMarket
import com.camo.kripto.utils.Resource
import com.camo.kripto.utils.Status
import timber.log.Timber
import javax.inject.Inject

class Repository @Inject constructor(private val db: AppDb, private val cgApiHelper: CGApiHelper) {

    suspend fun addFavCoin(favCoin: FavCoin) = db.favCoinDao().addFavCoin(favCoin)

    suspend fun removeFavCoin(id: String) = db.favCoinDao().removeFavCoin(id)

    suspend fun getFavCoins(): List<FavCoin> = db.favCoinDao().getFavCoins()

    suspend fun count(id: String): Int = db.favCoinDao().count(id)

    suspend fun addCurrencies(currencies: List<Currency>) {
        db.currencyDao().deleteAllCurrencies()
        db.currencyDao().addCurrencies(currencies)
    }

    suspend fun getCurrencies(): List<Currency> = db.currencyDao().getCurrencies()

    suspend fun lIRcurrencies(): Resource<Boolean> {
        val curr = ArrayList<Currency>()
        return try {
            val strings = getSupportedCurr()
            for (s in strings) {
                curr.add(Currency(s))
            }
            clearCurrencies()
            addCurrencies(curr)
            Resource(Status.SUCCESS, true, "success")
        } catch (e: Exception) {
            Timber.d(e.message.toString())
            Resource(Status.ERROR, false, e.message)

        }
    }

    suspend fun lIRCoins(): Resource<Boolean> {
        val coins: ArrayList<Coin>
        return try {
            coins = getCoins()
            clearCoins()
            addCoins(coins)
            Resource(Status.SUCCESS, true, "success")
        } catch (e: Exception) {
            Timber.d(e.message.toString())
            Resource(Status.ERROR, false, e.message)
        }
    }

    suspend fun addCoins(coins: ArrayList<Coin>) {
        db.coinDao().addCoins(coins)
    }

    suspend fun clearCurrencies() {
        db.currencyDao().deleteAllCurrencies()
    }

    suspend fun clearCoins() {
        db.coinDao().deleteAllCoins()
    }

    suspend fun getCoins() = cgApiHelper.getCoins()

    suspend fun getCoinFilterByName(name: String) = db.coinDao().getCoinFilterByName("%$name%")

    suspend fun getCurrentData(id: String) = cgApiHelper.getCurrentData(id)

    suspend fun getSupportedCurr() = cgApiHelper.getSupportedCurr()

    suspend fun getMarketChart(id: String, curr: String, days: String) =
        cgApiHelper.getMarketChart(id, curr, days)

    suspend fun getMarketCap(
        curr: String?,
        page: Int,
        order: String?,
        duration: String?,
        coins: List<CoinIdName>?
    ): List<CoinMarket.CoinMarketItem> =
        cgApiHelper.getMarketCap(curr, page, order, duration, coins)

    suspend fun getCurrCount() = db.currencyDao().count()

    suspend fun getTrending() = cgApiHelper.getTrending()

    suspend fun getGlobal() = cgApiHelper.getGlobal()

    suspend fun getExchanges(page: Int) = cgApiHelper.getExchanges(page)

    suspend fun getGlobalDefi() = cgApiHelper.getGlobalDefi()

    suspend fun pingCG() = cgApiHelper.ping()
}