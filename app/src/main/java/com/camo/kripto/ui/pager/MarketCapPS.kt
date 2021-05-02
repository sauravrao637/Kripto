package com.camo.kripto.ui.pager

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.model.CoinMarket

class MarketCapPS(
    private val backend: CGApiHelper,
    private val curr: String,
    private val order: Int
) :
    PagingSource<Int, CoinMarket.CoinMarketItem>() {
    private val TAG = MarketCapPS::class.simpleName
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CoinMarket.CoinMarketItem> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = backend.getMarketCap(curr, nextPageNumber, order)
            return LoadResult.Page(
                data = response,
                prevKey = params.key,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            Log.d(TAG, e.toString())
            return LoadResult.Error(e);
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CoinMarket.CoinMarketItem>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}