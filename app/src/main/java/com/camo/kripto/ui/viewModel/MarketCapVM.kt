package com.camo.kripto.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.camo.kripto.remote.model.CoinMarket
import com.camo.kripto.remote.model.Exchanges
import com.camo.kripto.remote.model.Trending
import com.camo.kripto.local.model.CoinIdName
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.pager.ExchangesPS
import com.camo.kripto.ui.pager.MarketCapPS
import com.camo.kripto.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MarketCapVM @Inject constructor(
    private val cgRepo:Repository
) : ViewModel() {
    var intialized = false
    var arr: Array<String>? = null
    var prefCurrency: MutableLiveData<String> = MutableLiveData<String>()
    var orderby: MutableLiveData<String> = MutableLiveData<String>()
    var duration: MutableLiveData<String> = MutableLiveData<String>()
    var trending: MutableLiveData<Resource<Trending>> = MutableLiveData()
    var currentFrag: Int? = null

    fun setValues(prefCurr: String,
                  dur: String,
                  prefOrder: String){
        orderby.postValue(prefOrder)
        duration.postValue(dur)
        prefCurrency.postValue(prefCurr)
    }

    fun getMarketCap(
        currency: String?,
        order: String?,
        dur: String?,
        coins: List<CoinIdName>?
    ): Flow<PagingData<CoinMarket.CoinMarketItem>> {
        return Pager(
            PagingConfig(pageSize = 25)
        ) {
            MarketCapPS(cgRepo,currency, order, dur, coins)
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
