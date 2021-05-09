package com.camo.kripto.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.camo.kripto.data.model.CoinMarket
import com.camo.kripto.data.model.Trending
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.database.model.CoinIdName
import com.camo.kripto.ui.pager.MarketCapPS
import com.camo.kripto.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MarketCapVM(private val cgRepo: CGRepo) : ViewModel() {
    lateinit var arr: Array<String>


    var prefCurrency: MutableLiveData<String> = MutableLiveData()
    var orderby: MutableLiveData<String> = MutableLiveData()
    var duration: MutableLiveData<String> = MutableLiveData()
    var trending: MutableLiveData<Resource<Trending>> = MutableLiveData()

    fun getMarketCap(currency: String?, order: String?, dur: String?, coins: List<CoinIdName>?): Flow<PagingData<CoinMarket.CoinMarketItem>> {
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(pageSize = 25)
        ) {
            MarketCapPS(cgRepo, currency, order,dur,coins)
        }.flow.cachedIn(viewModelScope)
    }

    fun durationArr(arr: Array<String>) {
        this.arr = arr
    }

    fun getTrending(): Flow<Resource<Trending>> {
        return  flow {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = cgRepo.getTrending()))
            } catch (exception: Exception) {
                exception.printStackTrace()
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    companion object{
        private val TAG = MarketCapVM::class.simpleName
    }
}
