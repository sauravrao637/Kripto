package com.camo.kripto.remote.api

import com.camo.kripto.local.model.CoinIdName
import com.camo.kripto.local.model.PriceAlert
import com.camo.kripto.remote.model.*
import retrofit2.Response
import java.math.BigDecimal
import javax.inject.Inject

class CGApiHelper @Inject constructor(private val cgService: CGService) : CGApiHelperIF {

    //    id_asc, id_desc
    override suspend fun getCoins() = cgService.getCoins()

    override suspend fun getSupportedCurr() = cgService.getSupportedCurr()

    override suspend fun getMarketCap(
        curr: String?,
        page: Int,
        order: String?,
        duration: String?,
        ids: List<CoinIdName>?
    ): List<CoinMarket.CoinMarketItem> {
        var s = ""
        if (ids != null) {
            if (ids.isEmpty()) return listOf()
            for (i in ids) {
                s += i.id + ","
            }
        }
        return cgService.getMarketCap(
            curr ?: "inr",
            25,
            page,
            order ?: "market_cap_desc",
            duration ?: "1h",
            s
        )
    }

    override suspend fun getCurrentData(id: String): CoinCD {
        return cgService.getCoinCD(
            id, "true", tickers = false, market_data = true, communityData = false,
            developer_data = false
        )
    }

    override suspend fun getMarketChart(
        id: String,
        curr: String,
        days: String
    ): Response<MarketChart> {
        return cgService.getCoinMarketChart(id, curr, days)
    }

    override suspend fun getTrending() = cgService.getTrending()

    override suspend fun getGlobal() = cgService.getGlobal()

    override suspend fun getExchanges(page: Int) = cgService.getExchanges(25, page)

    override suspend fun getGlobalDefi() = cgService.getGlobalDefi()

    override suspend fun ping() = cgService.ping()

    override suspend fun getExchangeRates(): Response<ExchangeRates> = cgService.getExchangeRates()

    override suspend fun getNews(category: String, projectType: String, page: Int): Response<News> =
        cgService.getNews(category, projectType, 25, page)

    //TODO add params
    override suspend fun getSimplePrice(
        coinIds: List<String>,
        currencies: List<String>
    ): Response<Map<String, Map<String, BigDecimal>>> {
//      we need atleast one coin and one curr to make a call so let's a coin,curr that doesn't exist
        val coinString = StringBuilder()
        val currString = StringBuilder()
        coinString.append("chutiya,")
        currString.append("chutiya,")
        for (s in coinIds) {
            coinString.append(s)
            coinString.append(",")
        }
        for (s in currencies) {
            currString.append(s)
            currString.append(",")
        }
        return cgService.getSimplePrice(
            coinString.toString(),
            currString.toString(),
            "true",
            "true",
            "true",
            "true"
        )
    }
}