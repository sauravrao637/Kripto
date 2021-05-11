package com.camo.kripto.ui.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.camo.kripto.data.model.CoinMarket
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.database.model.CoinIdName
import timber.log.Timber

class MarketCapPS(
    private val backend: CGRepo,
    private val curr: String?,
    private val order: String?,
    private val duration: String?,
    private val coins:List<CoinIdName>?
) :
    PagingSource<Int, CoinMarket.CoinMarketItem>() {



    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CoinMarket.CoinMarketItem> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = backend.getMarketCap(curr, nextPageNumber, order,duration,coins)
            var nextKey :Int? = nextPageNumber+1
            if(response.isEmpty()) nextKey = null
            LoadResult.Page(
                data = response,
                prevKey = params.key,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Timber.d( e.toString())
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CoinMarket.CoinMarketItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


}