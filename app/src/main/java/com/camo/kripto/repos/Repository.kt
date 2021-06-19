package com.camo.kripto.repos

import com.camo.kripto.error.ErrorCause
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.local.AppDb
import com.camo.kripto.local.model.*
import com.camo.kripto.remote.api.CGApiHelper
import com.camo.kripto.remote.model.CoinMarket
import com.camo.kripto.remote.model.ExchangeRates
import com.camo.kripto.remote.model.MarketChart
import com.camo.kripto.remote.model.Trending
import com.camo.kripto.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject

class Repository @Inject constructor(private val db: AppDb, val cgApiHelper: CGApiHelper) {

    suspend fun addFavCoin(favCoin: FavCoin) = db.favCoinDao().addFavCoin(favCoin)

    suspend fun removeFavCoin(id: String) = db.favCoinDao().removeFavCoin(id)

    suspend fun getFavCoins(): List<FavCoin> = db.favCoinDao().getFavCoins()

    suspend fun coinCountByID(id: String): Int = db.favCoinDao().count(id)

    suspend fun addCurrencies(currencies: List<Currency>) {
        db.currencyDao().deleteAllCurrencies()
        db.currencyDao().addCurrencies(currencies)
    }

    suspend fun getCurrencies(): List<Currency> = db.currencyDao().getCurrencies()

    suspend fun lIRCurrencies(): Resource<Boolean> {
        val curr = ArrayList<Currency>()
        return try {
            val strings = getSupportedCurr()
            for (s in strings) {
                curr.add(Currency(s))
            }
            clearCurrencies()
            addCurrencies(curr)
            Resource.success(true)
        } catch (e: Exception) {
            Timber.d(e.message.toString())
            Resource.error(false, ErrorInfo(e, ErrorCause.LOAD_IN_ROOM_CURRENCIES))
        }
    }

    suspend fun lIRCoins(): Resource<Boolean> {
        val coins: ArrayList<Coin>
        return try {
            coins = getCoins()
            clearCoins()
            addCoins(coins)
            Resource.success(true)
        } catch (e: Exception) {
            Timber.d(e.message.toString())
            Resource.error(false, ErrorInfo(e, ErrorCause.LOAD_IN_ROOM_COINS))
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

    suspend fun getCoinFilterByName(name: String): List<Coin> {
        return db.coinDao().getCoinFilterByName("%$name%")
    }

    class CoinWithFavStatus(val coin: Coin, var isFav: Boolean)

    suspend fun getCurrentData(id: String) = cgApiHelper.getCurrentData(id)

    suspend fun getSupportedCurr() = cgApiHelper.getSupportedCurr()

    suspend fun getMarketChart(
        id: String,
        curr: String,
        days: String
    ): Flow<Resource<MarketChart>> {
        return flow {
            emit(Resource.loading(null))
            try {
                val res = cgApiHelper.getMarketChart(id, curr, days)
                if (res.isSuccessful && res.code() == 200) {
                    if (res.body() != null) emit(Resource.success(res.body()!!))
                    else emit(Resource.error(null, ErrorInfo(null, ErrorCause.GET_MARKET_CHART)))
                } else {
                    emit(Resource.error(null, ErrorInfo(null, ErrorCause.GET_MARKET_CHART)))
                }
            } catch (e: java.lang.Exception) {
                Timber.d(e)
                emit(Resource.error(null, ErrorInfo(e, ErrorCause.GET_MARKET_CHART)))
            }
        }
    }

    suspend fun getMarketCap(
        curr: String?,
        page: Int,
        order: String?,
        duration: String?,
        coins: List<CoinIdName>?
    ): List<CoinMarket.CoinMarketItem> =
        cgApiHelper.getMarketCap(curr, page, order, duration, coins)

    suspend fun getCurrCount() = db.currencyDao().count()

    suspend fun getTrending(): Flow<Resource<Response<Trending>>> {
        return flow {
            emit(Resource.loading(null))
            try {
                val res = cgApiHelper.getTrending()
                if (res.isSuccessful && res.code() == 200) emit(Resource.success(res))
                else {
                    emit(Resource.error(null, ErrorInfo(null, ErrorCause.GET_TRENDING)))
                }
            } catch (e: Exception) {
                emit(Resource.error(null, ErrorInfo(e, ErrorCause.GET_TRENDING)))
            }
        }
    }


    suspend fun getGlobal() = cgApiHelper.getGlobal()

    suspend fun getExchanges(page: Int) = cgApiHelper.getExchanges(page)

    suspend fun getGlobalDefi() = cgApiHelper.getGlobalDefi()

    suspend fun pingCG(): Flow<Resource<Response<Any>>> {
        return flow {
            emit(Resource.loading(data = null))
            try {
                val res = cgApiHelper.ping()
                Timber.d(res.toString())
                if (res.isSuccessful && res.code() == 200) emit(Resource.success(res))
                else {
                    Timber.d(res.toString())
                    emit(Resource.error(res, ErrorInfo(null, ErrorCause.PING_CG)))
                }
            } catch (e: Exception) {
                Timber.d(e)
                emit(Resource.error(null, ErrorInfo(e, ErrorCause.PING_CG)))
            }
        }
    }

    suspend fun close() {
        withContext(Dispatchers.IO) {
            db.close()
        }
    }

    suspend fun getExchangeRates(): Flow<Resource<Response<ExchangeRates>>> {
        Timber.d("getExchangeRates called")
        return flow {
            emit(Resource.loading(null))
            try {
                val res = cgApiHelper.getExchangeRates()
                if (res.isSuccessful && res.code() == 200) {
                    emit(Resource.success(res))
                } else {
                    Timber.d(res.toString())
                    emit(Resource.error(res, ErrorInfo(res.errorBody())))
                }
            } catch (e: Exception) {
                Timber.d(e)
                emit(Resource.error(null, ErrorInfo(e, ErrorCause.EXCHANGE_RATE)))
            }
        }
    }

    suspend fun getAllAlerts(enabledStatus: Boolean?): List<PriceAlert> {
        Timber.d("getAllAlerts called")
        return if (enabledStatus == null)
            db.priceAlertDao().getAllAlerts()
        else db.priceAlertDao().getPriceAlertsByEnabledStatus(enabledStatus)
    }

    suspend fun getAllAlertsAsFlow(enabledStatus: Boolean?): Flow<Resource<List<PriceAlert>>> {
        Timber.d("getAllAlertsAsFlow called")
        return flow {
            emit(Resource.loading(null))
            try {
                val res = if (enabledStatus == null) db.priceAlertDao()
                    .getAllAlerts() else db.priceAlertDao()
                    .getPriceAlertsByEnabledStatus(enabledStatus)
                emit(Resource.success(res))
            } catch (e: Exception) {
                Timber.d(e)
                emit(Resource.error(null, ErrorInfo(e, ErrorCause.ROOM_ERROR)))
            }
        }
    }

    suspend fun crunchLatestPricesForAlerts(): Resource<Response<Map<String, Map<String, BigDecimal>>>> {
        Timber.d("crunchLatestPricesForAlerts called")
        val alerts = getAllAlerts(true)
        val coinCurrPair = getCoinCurrSets(alerts)
        return try {
            val res = cgApiHelper.getSimplePrice(
                coinCurrPair.first.toList(),
                coinCurrPair.second.toList()
            )
            if (res.isSuccessful && res.code() == 200) {
                Resource.success(res)
            } else {
                Resource.error(res, ErrorInfo(res.errorBody()))
            }

        } catch (e: Exception) {
            Resource.error(null, ErrorInfo(e, ErrorCause.GET_SIMPLE_PRICE))
        }
    }

    suspend fun getTriggered(): List<Pair<PriceAlert, Map<String, BigDecimal>>>? {
        Timber.d("getTriggered called")
        val latestPrices = crunchLatestPricesForAlerts().data?.body()
        val listToReturn = mutableListOf<Pair<PriceAlert, Map<String, BigDecimal>>>()
        return if (latestPrices != null) {
            val activeAlerts = getAllAlerts(true)
            for (alert in activeAlerts) {
                val value = latestPrices[alert.id] ?: continue
                val current = value[alert.curr] ?: continue
                if (alert.isTriggerOnceOnly) {
                    if (current < BigDecimal(alert.lessThan) || current > BigDecimal(alert.moreThan)) {
                        db.priceAlertDao().updateEnabled(alert.primaryKey, false)
                        listToReturn.add(Pair(alert, value))
                    }
                } else {
                    if (alert.shown) {
                        if (!(current < BigDecimal(alert.lessThan) || current > BigDecimal(alert.moreThan))) {
                            db.priceAlertDao().updateShown(alert.primaryKey, false)
                        }
                    } else {
                        if (current < BigDecimal(alert.lessThan) || current > BigDecimal(alert.moreThan)) {
                            db.priceAlertDao().updateShown(alert.primaryKey, true)
                            listToReturn.add(Pair(alert, value))
                        }
                    }
                }
            }
            listToReturn
        } else null
    }

    fun getCoinCurrSets(alertList: List<PriceAlert>): Pair<Set<String>, Set<String>> {
        Timber.d("getCoinCurrSets called")
        val set = mutableSetOf<String>()
        val setCurr = mutableSetOf<String>()
        for (alert in alertList) {
            set.add(alert.id)
            setCurr.add(alert.curr)
        }
        return Pair(set, setCurr)
    }

    suspend fun insertPriceAlert(
        id: String,
        name: String,
        curr: String,
        lessThan: String,
        moreThan: String,
        isTriggerOnceOnly: Boolean
    ) {
        Timber.d("insertPriceAlert called")
        var l = lessThan
        var m = moreThan
        if (lessThan.isEmpty()) l = "0"
        if (moreThan.isEmpty()) m = (Double.MAX_VALUE).toString()
        val priceAlert = PriceAlert(
            id,
            name,
            curr,
            l,
            m,
            enabled = true,
            shown = false,
            isTriggerOnceOnly = isTriggerOnceOnly
        )
        db.priceAlertDao().addPriceAlert(priceAlert)
    }

    suspend fun getSimplePrice(
        id: String,
        name: String
    ): Flow<Resource<Response<Map<String, Map<String, BigDecimal>>>>> {
        Timber.d("getSimplePrice called")
        return flow {
            emit(Resource.loading(null))
            try {
                val currencies = db.currencyDao().getCurrencies()
                val l = mutableListOf<String>()
                for (curr in currencies) {
                    l.add(curr.id)
                }
                val res = cgApiHelper.getSimplePrice(listOf(id), l)
                if (res.isSuccessful && res.code() == 200)
                    emit(Resource.success(res))
                else emit(Resource.error(res, ErrorInfo(res.errorBody())))
            } catch (e: Exception) {
                emit(Resource.error(null, ErrorInfo(e, ErrorCause.GET_SIMPLE_PRICE)))
            }
        }
    }

    suspend fun getPriceAlertsCount(): Int {
        return getTriggered()?.size ?: 0
    }

    suspend fun removePriceAlert(primaryKey: Long) {
        db.priceAlertDao().removePriceAlert(primaryKey)
    }

    fun getPriceAlerts(): Flow<List<PriceAlert>> {
        return db.priceAlertDao().getAllAlertsAsFlow()
    }

    suspend fun setPriceAlertEnabled(l: Long, boolean: Boolean) {
        db.priceAlertDao().updateEnabled(l,boolean)
    }
}


