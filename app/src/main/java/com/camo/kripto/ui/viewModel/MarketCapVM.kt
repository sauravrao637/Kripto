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
import com.camo.kripto.ui.pager.MarketCapPS
import kotlinx.coroutines.flow.Flow

class MarketCapVM : ViewModel() {
    private val TAG = MarketCapVM::class.simpleName

    var prefCurrency: MutableLiveData<String> = MutableLiveData()

    fun getMarketCap(s: String?): Flow<PagingData<CoinMarket.CoinMarketItem>> {
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(pageSize = 25)
        ) {
            MarketCapPS(CGApiHelper(RetrofitBuilder.CG_SERVICE), s?:"inr")
        }.flow.cachedIn(viewModelScope)
    }
}
