package com.camo.kripto.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.camo.kripto.R
import com.camo.kripto.data.model.CoinMarket
import com.camo.kripto.data.model.Exchanges
import com.camo.kripto.data.model.Trending
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.database.model.CoinIdName
import com.camo.kripto.ui.pager.ExchangesPS
import com.camo.kripto.ui.pager.MarketCapPS
import com.camo.kripto.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MarketCapVM(
    private val cgRepo: CGRepo,
    private val prefCurr: String,
    duration: String,
    prefOrder: String
) : ViewModel() {
    var arr: Array<String>? = null


    var prefCurrency: MutableLiveData<String> = MutableLiveData(prefCurr)
    var orderby: MutableLiveData<String> = MutableLiveData(prefOrder)
    var duration: MutableLiveData<String> = MutableLiveData(duration)
    var trending: MutableLiveData<Resource<Trending>> = MutableLiveData()
    var currentFrag: Int? = null


    fun getMarketCap(
        currency: String?,
        order: String?,
        dur: String?,
        coins: List<CoinIdName>?
    ): Flow<PagingData<CoinMarket.CoinMarketItem>> {
        return Pager(
            PagingConfig(pageSize = 25)
        ) {
            MarketCapPS(cgRepo, currency, order, dur, coins)
        }.flow.cachedIn(viewModelScope)
    }


    fun getTrending(): Flow<Resource<Trending>> {
        return flow {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = cgRepo.getTrending()))
            } catch (exception: Exception) {
                exception.printStackTrace()
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }


    fun getExchanges(): Flow<PagingData<Exchanges.ExchangesItem>> {
        return Pager(
            PagingConfig(pageSize = 25)
        ) {
            ExchangesPS(cgRepo)
        }.flow.cachedIn(viewModelScope)
    }

    fun toggleDuration() {
        if (arr != null) {
            var i = arr!!.indexOf(duration.value)
            i = (i + 1) % 7
            duration.postValue(arr!![i])
        }
    }

}
