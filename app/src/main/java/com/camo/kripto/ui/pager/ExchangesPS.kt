package com.camo.kripto.ui.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.camo.kripto.remote.model.Exchanges
import com.camo.kripto.repos.Repository
import timber.log.Timber
import javax.inject.Inject

class ExchangesPS @Inject constructor(private val backend: Repository) :
    PagingSource<Int, Exchanges.ExchangesItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Exchanges.ExchangesItem> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = backend.getExchanges(nextPageNumber)
            var nextKey: Int? = nextPageNumber + 1
            if (response.isEmpty()) nextKey = null
            LoadResult.Page(
                data = response,
                prevKey = params.key,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Timber.d(e.toString())
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Exchanges.ExchangesItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}