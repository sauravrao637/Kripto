package com.camo.kripto.ui.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.camo.kripto.error.InvalidResponse
import com.camo.kripto.remote.api.CGApiHelperIF
import com.camo.kripto.remote.model.News
import timber.log.Timber
import javax.inject.Inject

class NewsPS @Inject constructor(private val backend: CGApiHelperIF, private val category: String) :
    PagingSource<Int, News.StatusUpdate>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News.StatusUpdate> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = backend.getNews(category = category, "",nextPageNumber)
            var nextKey :Int? = nextPageNumber+1
            val list = response.body()?.status_updates?: emptyList()
            if(list.isEmpty()) nextKey = null
            if(response.isSuccessful && response.code()==200)
            LoadResult.Page(
                data = list,
                prevKey = params.key,
                nextKey = nextKey
            )
            else{
                LoadResult.Error(InvalidResponse())
            }
        } catch (e: Exception) {
            Timber.d( e.toString())
            //TODO correct
            LoadResult.Error(InvalidResponse())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, News.StatusUpdate>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}