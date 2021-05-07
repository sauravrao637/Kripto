package com.camo.kripto.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.data.model.CoinMarket
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.database.model.CoinIdName
import com.camo.kripto.database.model.FavCoin
import com.camo.kripto.ui.pager.MarketCapPS
import kotlinx.coroutines.flow.Flow


class MarketCapVM(private val cgRepo: CGRepo) : ViewModel() {
    lateinit var arr: Array<String>
    private val TAG = MarketCapVM::class.simpleName

    var prefCurrency: MutableLiveData<String> = MutableLiveData()
    var orderby: MutableLiveData<Int> = MutableLiveData()
    var duration: MutableLiveData<Int> = MutableLiveData()

    fun getMarketCap(s: String?, order: Int, duration: Int,coins: List<CoinIdName>?): Flow<PagingData<CoinMarket.CoinMarketItem>> {
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(pageSize = 50)
        ) {
            MarketCapPS(cgRepo, s ?: "inr", order, duration,coins)
        }.flow.cachedIn(viewModelScope)
    }

    fun durationArr(arr: Array<String>) {
        this.arr = arr
    }
}
