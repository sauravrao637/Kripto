package com.camo.kripto.repos

import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.error.ErrorCause
import com.camo.kripto.local.AppDb
import com.camo.kripto.local.model.Coin
import com.camo.kripto.local.model.CoinIdName
import com.camo.kripto.local.model.Currency
import com.camo.kripto.local.model.FavCoin
import com.camo.kripto.remote.api.CGApiHelper
import com.camo.kripto.remote.model.CoinMarket
import com.camo.kripto.remote.model.MarketChart
import com.camo.kripto.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class Repository @Inject constructor(private val db: AppDb, private val cgApiHelper: CGApiHelper) {

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

    suspend fun getCoinFilterByName(name: String): List<CoinWithFavStatus> {
        val list = ArrayList<CoinWithFavStatus>()
        for (coin in db.coinDao().getCoinFilterByName("%$name%")) {
            if(coinCountByID(coin.id)>0)list.add(CoinWithFavStatus(coin,true))
            else list.add(CoinWithFavStatus(coin,false))
        }
        return list
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

    suspend fun getTrending() = cgApiHelper.getTrending()

    suspend fun getGlobal() = cgApiHelper.getGlobal()

    suspend fun getExchanges(page: Int) = cgApiHelper.getExchanges(page)

    suspend fun getGlobalDefi() = cgApiHelper.getGlobalDefi()

    suspend fun pingCG(): Flow<Resource<Response<Any>>> {
        return flow {
            emit(Resource.loading(data = null))
            try {
                val res = cgApiHelper.ping()
                if (res.isSuccessful && res.code() == 200) emit(Resource.success(res))
                else emit(Resource.error(res, ErrorInfo(null, ErrorCause.PING_CG)))
            } catch (e: Exception) {
                Timber.d(e)
                emit(Resource.error(null, ErrorInfo(e, ErrorCause.PING_CG)))
            }
        }
    }
}