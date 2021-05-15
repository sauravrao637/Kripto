package com.camo.kripto.remote.api

import com.camo.kripto.remote.model.CoinCD
import com.camo.kripto.remote.model.CoinMarket
import com.camo.kripto.remote.model.MarketChart
import com.camo.kripto.local.model.CoinIdName
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
        if (ids != null && ids.isNotEmpty()) {
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

    override suspend fun getMarketChart(id: String, curr: String, days: String): MarketChart {
        return cgService.getCoinMarketChart(id, curr, days)
    }

    override suspend fun getTrending() = cgService.getTrending()

    override suspend fun getGlobal() = cgService.getGlobal()

    override suspend fun getExchanges(page: Int) = cgService.getExchanges(25, page)

    override suspend fun getGlobalDefi() = cgService.getGlobalDefi()
}